package lithium.service.cashier.processor.emerchant;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.NotificationGateway;
import com.emerchantpay.gateway.api.constants.Endpoints;
import com.emerchantpay.gateway.api.constants.Environments;
import com.emerchantpay.gateway.api.requests.wpf.WPFReconcileRequest;
import com.emerchantpay.gateway.util.Configuration;
import com.emerchantpay.gateway.util.NodeWrapper;
import com.emerchantpay.gateway.util.SHA512Hasher;
import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.emerchant.TransactionStatus;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		final String WPF_PREFIX = "wpf_";
		log.debug("emerchant :: ProcessorCallback");
		log.debug("Req: " + request);
		log.debug("Res: " + response);

		Long tranId = (request.getParameter(WPF_PREFIX + "transaction_id") != null) ? Long.parseLong(request.getParameter(WPF_PREFIX + "transaction_id")) : -1L;
		Response<DoProcessorRequest> doProcessorRequest = getDoProcessorRequest(tranId, request.getProcessorCode());
		if (!doProcessorRequest.isSuccessful()) {
			throw new Exception(doProcessorRequest.getMessage());
		}

		String uniqueId = request.getParameter(WPF_PREFIX + "unique_id");
		String password = doProcessorRequest.getData().getProperty("password");
		String sha512Hasher = SHA512Hasher.SHA512(uniqueId + password);
		String signature = request.getParameter("signature");

		if (!sha512Hasher.equalsIgnoreCase(signature)) {
			log.error("Invalid signature received - Decline. TranId: " + tranId + " ExtTranId: " + uniqueId);
			response.setMessage("Invalid signature received.");
			response.setStatus(DoProcessorResponseStatus.DECLINED);
			return response;
		}

		TransactionStatus status = TransactionStatus.fromStatus(request.getParameter(WPF_PREFIX + "status"));

		switch (status) {
			case APPROVED:
				response.setStatus(DoProcessorResponseStatus.SUCCESS);
				break;
			case DECLINED:
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				break;
			case PENDING_ASYNC:
			case PENDING:
			case NEW:
				response.setStatus(DoProcessorResponseStatus.NOOP);
				break;
			case ERROR:
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				break;
			case TIMEOUT:
				response.setStatus(DoProcessorResponseStatus.EXPIRED);
				break;
			default:
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				break;
		}

		if (request.getParameter("payment_transaction_amount") != null) {
			BigDecimal amountReceived = new BigDecimal(request.getParameter("payment_transaction_amount"));
			response.setAmountCentsReceived(amountReceived.intValue());
		}

		response.setTransactionId(tranId);
		response.setProcessorReference(uniqueId);
		response.setProcessorRequest(request.getParameterMap());
		response.setProcessorUserId(request.getParameter("consumer_id"));

		response.setOutputData(2, "payment_transaction_terminal_token", request.getParameter("payment_transaction_terminal_token"));
		response.setOutputData(2, "payment_transaction_token", request.getParameter("payment_transaction_token"));
		response.setOutputData(2, "payment_transaction_avs_response_code", request.getParameter("payment_transaction_avs_response_code"));
		response.setOutputData(2, "payment_transaction_avs_response_text", request.getParameter("payment_transaction_avs_response_text"));
		response.setOutputData(2, "payment_transaction_transaction_type", request.getParameter("payment_transaction_transaction_type"));
		response.setOutputData(2, "eci", request.getParameter("eci"));

		//response.setCallbackResponse("<?xml version=\"1.0\" encoding=\"UTF-8\"?><notification_echo><unique_id>" + uniqueId + "</unique_id></notification_echo>");
		response.setCallbackResponse(recon(request, doProcessorRequest.getData(), uniqueId, response));
		return response;
	}

	private  String recon(DoProcessorCallbackRequest cbRequest, DoProcessorRequest request, String uniqueId, DoProcessorCallbackResponse cbResponse) throws Exception {
		Environments env = Environments.STAGING;
		String envProperty = request.getProperty("environment");
		if ((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty))) env = Environments.PRODUCTION;

		Configuration configuration = new Configuration(env, Endpoints.EMERCHANTPAY);
		configuration.setUsername(request.getProperty("username"));
		configuration.setPassword(request.getProperty("password"));
		configuration.setToken(request.getProperty("token"));
		configuration.setDebugMode(((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty)))?false:true);
		configuration.setWpfEnabled(true);

		NotificationGateway gw = new NotificationGateway(configuration, cbRequest.getParameterMap());
		gw.parseNotification(cbRequest.getParameterMap());
		WPFReconcileRequest wpfReconcileRequest = new WPFReconcileRequest();
		wpfReconcileRequest.setUniqueId(uniqueId);
		GenesisClient client = new GenesisClient(configuration, wpfReconcileRequest);
		client.execute();
		NodeWrapper nodeWrapper = client.getResponse();
		cbResponse.addRawResponseLog("Recon response: " + nodeWrapper.toString());
		log.debug("Recon response: " + nodeWrapper.toString());
//		gw.initReconciliation(); //buggy on emerchant side
		gw.generateResponse();
		return gw.getResponse().toXML();
	}
}
