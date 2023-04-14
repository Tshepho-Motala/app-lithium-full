package lithium.service.cashier.client.internal;

import lithium.service.cashier.client.frontend.DoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;

@FeignClient(name="service-cashier")
public interface DoCallbackClient {
	@RequestMapping(method = RequestMethod.POST, path="/internal/docallback")
	public Response<String> doCallback(@RequestBody DoProcessorResponse response);

	@RequestMapping(method = RequestMethod.POST, path="/internal/dosafecallback")
	public Response<DoResponse> doSafeCallback(@RequestBody DoProcessorResponse response);
	
	@RequestMapping(method = RequestMethod.POST, path="/internal/docallbackgettransaction")
	public Response<DoProcessorRequest> doCallbackGetTransaction(
		@RequestParam("transactionId") long transactionId,
		@RequestParam("processorCode") String processorCode
	);

	@RequestMapping(method = RequestMethod.POST, path="/internal/docallbackgettransactionfromreference")
	public Response<DoProcessorRequest> doCallbackGetTransactionByProcessorReference(
		@RequestParam("processorReference") String processorReference,
		@RequestParam("processorCode") String processorCode
	);

	@RequestMapping(method = RequestMethod.POST, path="/internal/do-callback-get-transaction-from-additional-reference")
	public Response<DoProcessorRequest> doCallbackGetTransactionByAdditionalReference(
		@RequestParam("additionalReference") String additionalReference,
		@RequestParam("processorCode") String processorCode
	);

	@RequestMapping(method = RequestMethod.POST, path="/internal/docallbackgettransactionfromreferenceoob")
	public Response<DoProcessorRequest> doCallbackGetTransactionByProcessorReference(
		@RequestParam("transactionId") long transactionId,
		@RequestParam("processorReference") String processorReference,
		@RequestParam("processorCode") String processorCode,
		@RequestParam(required = false, name = "checkOOB", defaultValue = "false") Boolean checkOOB
	);

	@RequestMapping(method = RequestMethod.GET, path="/populate-transactions-payment-methods-job")
	public Response<Void> populateTransactionsPaymentMethods(
			@RequestParam(name = "dryRun", defaultValue = "true") boolean dryRun,
			@RequestParam(name = "onePagePopulationFlag", defaultValue = "true") boolean onePagePopulationFlag,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "delay", defaultValue = "1000") Long delay
	);
}
