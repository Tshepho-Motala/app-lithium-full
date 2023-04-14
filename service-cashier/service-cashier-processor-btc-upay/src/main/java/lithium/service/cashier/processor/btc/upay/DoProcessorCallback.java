package lithium.service.cashier.processor.btc.upay;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;
import lithium.service.cashier.processor.upay.btc.data.IPNRequest;

@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {
	
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		IPNRequest processorRequest = convertToProcessorClass(request, IPNRequest.class);
		
		Long transactionId = Long.parseLong(processorRequest.getOrder_id());
		
		Response<DoProcessorRequest> doProcessorRequest = getDoProcessorRequest(transactionId, request.getProcessorCode());
		if (!doProcessorRequest.isSuccessful()) {
			throw new Exception(doProcessorRequest.getMessage());
		}
//		int confirmations = Integer.parseInt(doProcessorRequest.getData().getProperty("btcconfirmations"));
		
		if (!processorRequest.calculateSign(doProcessorRequest.getData().getProperty("apisecret")).equals(processorRequest.getSign())) {
			throw new Exception("Invalid signature");
		}
		
		response.setStatus(DoProcessorResponseStatus.NOOP);
		if (processorRequest.getStatus().equals("success")) {
			response.setStatus(DoProcessorResponseStatus.SUCCESS);
		}
		
		response.setTransactionId(transactionId);
		response.setProcessorReference(processorRequest.getTransaction_id());
		response.setProcessorRequest(processorRequest);
		response.setAmountCentsReceived(new BigDecimal(processorRequest.getSettled_amount()).multiply(new BigDecimal(100)).intValue());
		response.setCallbackResponse(response.getMessage());
		
		return response;
	}
}
