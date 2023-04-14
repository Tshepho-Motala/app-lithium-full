package lithium.service.cashier.processor.btc.globalbitlocker;

import java.math.BigDecimal;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.btc.globalbitlocker.data.CallbackResponse;
import lithium.service.cashier.processor.btc.globalbitlocker.data.ReceiveAddressRequest;
import lithium.service.cashier.processor.btc.globalbitlocker.enums.Status;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;

@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {
	@Autowired
	private LithiumConfigurationProperties config;
	
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		CallbackResponse callbackResponse = CallbackResponse.builder()
			.id(request.getParameter("id"))
			.amount(request.getParameter("amount"))
			.bitcoinAmount(request.getParameter("bitcoin_amount"))
			.status(request.getParameter("status"))
			.hash(request.getParameter("h"))
			.transactionId(Long.parseLong(request.getParameter("t")))
			.build();
		
		Response<DoProcessorRequest> doProcessorRequest = getDoProcessorRequest(callbackResponse.getTransactionId(), request.getProcessorCode());
		if (!doProcessorRequest.isSuccessful()) {
			throw new Exception(doProcessorRequest.getMessage());
		}
//		int confirmations = Integer.parseInt(doProcessorRequest.getData().getProperty("btcconfirmations"));
		
		// "http://196.22.242.139:9000"
		String callbackUrl = config.getGatewayPublicUrl()+
			doProcessorRequest.getData().getProperty("callback.url")+
			"?t="+callbackResponse.getTransactionId();
		ReceiveAddressRequest checkSignature = ReceiveAddressRequest.builder()
			.apiKey(doProcessorRequest.getData().getProperty("apikey"))
			.user(doProcessorRequest.getData().getUser().getGuid())
			.callbackUrl(URLEncoder.encode(callbackUrl, "UTF-8"))
			.build();
		
		if (!checkSignature.calculateSign().equals(callbackResponse.getHash())) {
			throw new Exception("Invalid signature");
		}
		
		response.setCallbackResponse("nok");
		switch (Status.fromStatus(callbackResponse.getStatus())) {
			case COMPLETED:
				response.setStatus(DoProcessorResponseStatus.SUCCESS);
				response.setMessage("Success");
				response.setCallbackResponse("*ok*");
				break;
			case EXPIRED:
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				response.setMessage("Expired");
				break;
			case WAITING:
				response.setMessage("Pending");
				response.setStatus(DoProcessorResponseStatus.NOOP);
				break;
			default:
				response.setMessage("Failed");
				response.setStatus(DoProcessorResponseStatus.FATALERROR);
		}
		
		response.setTransactionId(callbackResponse.getTransactionId());
		response.setProcessorReference(callbackResponse.getId());
		response.setProcessorRequest(callbackResponse);
		response.setAmountCentsReceived(new BigDecimal(callbackResponse.getAmount()).multiply(new BigDecimal(100)).intValue());
		
		return response;
	}	
}
