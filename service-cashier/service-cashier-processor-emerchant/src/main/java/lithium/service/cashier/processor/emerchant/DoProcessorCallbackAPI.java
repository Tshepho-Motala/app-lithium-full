package lithium.service.cashier.processor.emerchant;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.NotificationGateway;
import com.emerchantpay.gateway.api.constants.Endpoints;
import com.emerchantpay.gateway.api.constants.Environments;
import com.emerchantpay.gateway.api.requests.nonfinancial.reconcile.ReconcileRequest;
import com.emerchantpay.gateway.util.Configuration;
import com.emerchantpay.gateway.util.NodeWrapper;
import com.emerchantpay.gateway.util.SHA1Hasher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.emerchant.TransactionStatus;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;
import lombok.extern.slf4j.Slf4j;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Not used currently but could be used when we become PAI DSS compliant
 */
@Slf4j
//@Service
public class DoProcessorCallbackAPI extends DoProcessorCallbackAdapter {
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		log.debug("emerchant :: ProcessorCallback");
		log.debug("Req: "+request);
		log.debug("Res: "+response);

		Long tranId = (request.getParameter("transaction_id")!=null)?Long.parseLong(request.getParameter("transaction_id")):-1L;
		Response<DoProcessorRequest> doProcessorRequest = getDoProcessorRequest(tranId, request.getProcessorCode());
		if (!doProcessorRequest.isSuccessful()) {
			throw new Exception(doProcessorRequest.getMessage());
		}

		String uniqueId = request.getParameter("unique_id");
		String password = doProcessorRequest.getData().getProperty("password");
		String sha1Hasher = SHA1Hasher.SHA1(uniqueId+password);
		String signature = request.getParameter("signature");

		if (!sha1Hasher.equalsIgnoreCase(signature)) {
			log.error("Invalid signature received - Decline. TranId: "+tranId+" ExtTranId: "+uniqueId);
			response.setMessage("Invalid signature received.");
			response.setStatus(DoProcessorResponseStatus.DECLINED);
			return response;
		}

		TransactionStatus status = TransactionStatus.fromStatus(request.getParameter("status"));

		switch (status) {
			case APPROVED:
				response.setStatus(DoProcessorResponseStatus.SUCCESS);
				break;
			case DECLINED:
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				break;
			case PENDING_ASYNC:
			case PENDING:
				response.setStatus(DoProcessorResponseStatus.NOOP);
				break;
			case ERROR:
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				break;
			default:
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				break;
		}

		BigDecimal amountReceived = new BigDecimal(request.getParameter("amount"));

		response.setTransactionId(tranId);
		response.setProcessorReference(uniqueId);
		response.setAmountCentsReceived(amountReceived.intValue());
		response.setProcessorRequest(request.getParameterMap());
		response.setProcessorUserId(request.getParameter("consumer_id"));

		response.setOutputData(2, "token", request.getParameter("token"));
		response.setOutputData(2, "eci", request.getParameter("eci"));
		response.setOutputData(2, "avs_response_code", request.getParameter("avs_response_code"));
		response.setOutputData(2, "avs_response_text", request.getParameter("avs_response_text"));

		//response.setCallbackResponse("<?xml version=\"1.0\" encoding=\"UTF-8\"?><notification_echo><unique_id>" + uniqueId + "</unique_id></notification_echo>");
		response.setCallbackResponse(recon(request, response, doProcessorRequest.getData(), uniqueId));
		return response;
	}

	private  String recon(DoProcessorCallbackRequest cbRequest, DoProcessorCallbackResponse cbResponse, DoProcessorRequest request, String uniqueId) throws Exception {
		Environments env = Environments.STAGING;
		String envProperty = request.getProperty("environment");
		if ((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty))) env = Environments.PRODUCTION;

		Configuration configuration = new Configuration(env, Endpoints.EMERCHANTPAY);
		configuration.setUsername(request.getProperty("username"));
		configuration.setPassword(request.getProperty("password"));
		configuration.setToken(request.getProperty("token"));
		configuration.setDebugMode(((env != null) && ("PRODUCTION".equalsIgnoreCase(envProperty)))?false:true);

		NotificationGateway gw = new NotificationGateway(configuration, cbRequest.getParameterMap());
		gw.initReconciliation();
		gw.generateResponse();
		return gw.getResponse().toXML();
	}
}
