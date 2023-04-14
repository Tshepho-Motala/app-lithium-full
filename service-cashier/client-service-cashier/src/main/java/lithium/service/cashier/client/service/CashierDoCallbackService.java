package lithium.service.cashier.client.service;

import lithium.service.cashier.client.exceptions.Status515CallbackGetTransactionClientException;
import lithium.service.cashier.client.frontend.DoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.cashier.client.internal.DoCallbackClient;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.client.LithiumServiceClientFactory;

@Service
public class CashierDoCallbackService {

	@Autowired LithiumServiceClientFactory factory;
	
	public Response<String> doCallback(DoProcessorResponse response) throws Exception {
		DoCallbackClient client = factory.target(DoCallbackClient.class);
		return client.doCallback(response);
	}

	public Response<DoResponse> doSafeCallback(DoProcessorResponse response) throws Exception {
		DoCallbackClient client = factory.target(DoCallbackClient.class);
		return client.doSafeCallback(response);
	}
	
	public Response<DoProcessorRequest> doCallbackGetTransaction(
		long transactionId,
		String processorCode
	) throws Exception {
		DoCallbackClient client = factory.target(DoCallbackClient.class);
		return client.doCallbackGetTransaction(transactionId, processorCode);
	}

	public DoProcessorRequest getTransaction(
			long transactionId,
			String processorCode
	) throws Exception {
		DoCallbackClient client = factory.target(DoCallbackClient.class);
		Response<DoProcessorRequest> response = client.doCallbackGetTransaction(transactionId, processorCode);
		if (!response.isSuccessful()) throw new Status515CallbackGetTransactionClientException(
				response.getStatus() + " " +
				response.getMessage());
		return response.getData();
	}

	public Response<DoProcessorRequest> doCallbackGetTransaction(
		long transactionId,
		String processorReference,
		String processorCode,
		Boolean oob
	) throws Exception {
		DoCallbackClient client = factory.target(DoCallbackClient.class);
		return client.doCallbackGetTransactionByProcessorReference(transactionId, processorReference, processorCode, oob);
	}

	public Response<DoProcessorRequest> doCallbackGetTransactionFromProcessorReference(
			String processorReference,
			String processorCode
	) throws Exception {
		DoCallbackClient client = factory.target(DoCallbackClient.class);
		return client.doCallbackGetTransactionByProcessorReference(processorReference, processorCode);
	}

	public Response<DoProcessorRequest> doCallbackGetTransactionFromAdditionalReference(
			String additionalReference,
			String processorCode
	) throws Exception {
		DoCallbackClient client = factory.target(DoCallbackClient.class);
		return client.doCallbackGetTransactionByAdditionalReference(additionalReference, processorCode);
	}

	public void populateTransactionsPaymentMethods(
			boolean dryRun,
			boolean onePagePopulationFlag,
			int pageSize,
			Long delay
	) throws Exception {
		DoCallbackClient client = factory.target(DoCallbackClient.class);
		client.populateTransactionsPaymentMethods(dryRun, onePagePopulationFlag, pageSize, delay);
	}
}
