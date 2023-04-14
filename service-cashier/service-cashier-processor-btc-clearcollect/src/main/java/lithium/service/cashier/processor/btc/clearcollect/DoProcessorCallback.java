package lithium.service.cashier.processor.btc.clearcollect;

import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.btc.clearcollect.data.CallbackRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.RequestWithoutSignature;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {
	
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		RequestWithoutSignature<CallbackRequest> requestWithoutSignature = new RequestWithoutSignature<>();
		requestWithoutSignature.payloadFromHeaders(request, CallbackRequest.class);
		
		Long transactionId = Long.parseLong(requestWithoutSignature.getData().getClientTracking());
		Response<DoProcessorRequest> doProcessorRequest = getDoProcessorRequest(transactionId, request.getProcessorCode());
		int confirmations = Integer.parseInt(doProcessorRequest.getData().getProperty("btcconfirmations"));
		
		log.info("Received callback from processor: " + requestWithoutSignature.toString());
		
		switch (requestWithoutSignature.getData().getStatus()) {
			case "C":
				if (Integer.parseInt(requestWithoutSignature.getData().getConfirmations()) >= confirmations) {
					response.setMessage("Success");
					response.setStatus(DoProcessorResponseStatus.SUCCESS);
				} else {
					response.setMessage("Not enough confirmations");
					response.setStatus(DoProcessorResponseStatus.NOOP);
				}
				break;
			case "O":
				response.setMessage("Out of bounds");
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				break;
			case "P":
				response.setMessage("Pending");
				response.setStatus(DoProcessorResponseStatus.NOOP);
				break;
			case "E":
				response.setMessage("Double spent");
				response.setStatus(DoProcessorResponseStatus.FATALERROR);
				break;
			default:
				response.setStatus(DoProcessorResponseStatus.NOOP);
				response.setMessage("Success");
				break;
		}
		
		response.setTransactionId(transactionId);
		response.setProcessorReference(requestWithoutSignature.getData().getId());
		response.setProcessorRequest(requestWithoutSignature);
		response.setAmountCentsReceived(Integer.parseInt(requestWithoutSignature.getData().getCreditAmountUsdInt()));
		response.setCallbackResponse(response.getMessage());
		
		return response;
	}
}
