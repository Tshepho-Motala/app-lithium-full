package lithium.service.cashier.processor.callback;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.service.CashierDoCallbackService;

public class DoProcessorCallbackAdapter implements DoProcessorCallbackInterface {
	@Autowired
	private CashierDoCallbackService service;
	
	protected Response<DoProcessorRequest> getDoProcessorRequest(Long transactionId, String processorCode) throws Exception {
		Response<DoProcessorRequest> processorRequest = service.doCallbackGetTransaction(transactionId, processorCode);
		if (!processorRequest.isSuccessful()) {
			throw new Exception(processorRequest.getMessage());
		}
		return processorRequest;
	}

	protected Response<DoProcessorRequest> getDoProcessorRequest(Long transactionId, String processorReference, String processorCode, Boolean oob) throws Exception {
		Response<DoProcessorRequest> processorRequest = service.doCallbackGetTransaction(transactionId, processorReference, processorCode, oob);
		if (!processorRequest.isSuccessful()) {
			throw new Exception(processorRequest.getMessage());
		}
		return processorRequest;
	}

	protected <T> T convertToProcessorClass(DoProcessorCallbackRequest request, Class<T> responseType) {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.convertValue(request.getParameterMap(), responseType);
		
	}
	
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		throw new NotImplementedException("Callback is not implemented in this processor");
	}
}