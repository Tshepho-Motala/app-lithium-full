package lithium.service.cashier.client;

import lithium.service.Response;
import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.service.cashier.client.objects.DepositStatus;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.objects.DomainMethodProcessorProperty;
import lithium.service.cashier.client.objects.ProcessedProcessorProperty;
import lithium.service.cashier.client.objects.TransactionType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Map;

@FeignClient(name="service-cashier")
public interface CashierInternalClient {
	
	@RequestMapping(path="/internal/deposit/saveprocessingattempt", method= RequestMethod.POST)
	public Long saveProcessingAttempt(
			@RequestParam("transactionId") Long transactionId,
			@RequestParam("success") boolean success, 
			@RequestParam("messages") String messages,
			@RequestParam("rawResponse") String rawResponse,
			@RequestParam("reference") String reference);
	

	@RequestMapping(path="/internal/methods/{domainMethodProcessorId}/properties", method=RequestMethod.GET) 
	public Response<List<ProcessedProcessorProperty>> processorProperties(@PathVariable("domainMethodProcessorId") Long domainMethodProcessorId);

	@RequestMapping(path="/internal/processorByMethodCodeAndProcessorDescription", method=RequestMethod.GET)
	public Response<DomainMethodProcessor> processorByMethodCodeAndProcessorDescription(
			@RequestParam("domainName") String domainNames,
			@RequestParam("deposit") Boolean deposit,
			@RequestParam("methodCode") String methodCode,
			@RequestParam("processorDescription") String processorDescription);

	@RequestMapping(path="/internal/processor-properties-of-first-enabled-by-method-code", method=RequestMethod.GET)
	public Response<Map<String, String>> propertiesOfFirstEnabledProcessorByMethodCode(
			@RequestParam("domainName") String domainName,
			@RequestParam("deposit") Boolean deposit,
			@RequestParam("methodCode") String methodCode);

	@RequestMapping(path = "/internal/processor-properties-by-method-code-and-user-data", method = RequestMethod.GET)
	public Response<List<DomainMethodProcessorProperty>> propertiesOfFirstEnabledProcessor(
			@RequestParam("methodCode")  String methodCode, @RequestParam("deposit")  boolean deposit,
			@RequestParam("userGuid")  String userGuid, @RequestParam("domainName")  String domainName,
			@RequestParam("ipAddress")  String ipAddress, @RequestParam("userAgent")  String userAgent);

   @RequestMapping(path = "/internal/deposit/register", method = RequestMethod.GET)
   public Response<Long> registerDeposit(
           @RequestParam("domainMethodProcessorId") Long domainMethodProcessorId,
           @RequestParam("userGuid") String userGuid,
           @RequestParam("currencyCode") String currencyCode,
           @RequestParam("amountInCents") Long amountInCents,
           @RequestParam("reference") String reference,
           @RequestParam("additionalReference") String additionalReference,
           @RequestParam("sessionId") Long sessionId,
           @RequestParam("success") boolean success,
           @RequestParam("paymentType") String paymentType);

	@RequestMapping(path="/internal/deposit/status", method=RequestMethod.GET)
	public Response<DepositStatus> depositStatus(
			@RequestParam("processorReference") String processorReference,
			@RequestParam("processorCode") String processorCode);

	@RequestMapping(path="/internal/summary/pending/amount", method=RequestMethod.GET)
	public Response<Long> pendingAmountCents(
			@RequestParam("userGuid") String processorReference);

	@RequestMapping(path="/internal/restriction/hours-since-transaction", method=RequestMethod.GET)
	public Response<Long> hoursSinceTransaction(
			@RequestParam("userGuid") String userGuid,
			@RequestParam("transactionType") TransactionType transactionType);

	@RequestMapping(path="/internal/restriction/first-transaction-date", method=RequestMethod.GET)
	public Response<Date> firstTransactionDate(
			@RequestParam("userGuid") String userGuid,
			@RequestParam("transactionType") TransactionType transactionType);

	@RequestMapping(path="/internal/transaction-bulk-processing/proceed-codes", method=RequestMethod.GET)
	public Response<Long> proceedCodes(
			@RequestParam("userGuid") String guid,
			@RequestParam("actions") List<TransactionProcessingCode> actions,
			@RequestParam("comment") String comment
			);
}
