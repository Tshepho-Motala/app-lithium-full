package lithium.service.accounting.client;

import lithium.exceptions.Status425DateParseException;
import lithium.service.Response;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.AccountCode;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.accounting.objects.TransactionType;
import lithium.service.client.datatable.DataTableResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-accounting", path="/admin/transactions")
public interface AdminTransactionsClient {

	@RequestMapping("/table")
	public DataTableResponse<TransactionEntryBO> table(
		@RequestParam(name="dateRangeStart") @DateTimeFormat(pattern="yyyy-MM-dd") String dateRangeStart,
		@RequestParam(name="dateRangeEnd") @DateTimeFormat(pattern="yyyy-MM-dd") String dateRangeEnd,
		@RequestParam(name="userGuid") String userGuid,
		@RequestParam(name="transactionId") String transactionId,
		@RequestParam(name="draw") String draw,
		@RequestParam(name="start") int start,
		@RequestParam(name="length") int length,
		@RequestParam(name="searchValue") String searchValue,
		@RequestParam(name="providerGuid") String providerGuid,
		@RequestParam(name="providerTransId") String providerTransId,
		@RequestParam(name="transactionType") List<String> transactionType,
		@RequestParam(name="additionalTransId", required = false) String additionalTransId,
		@RequestParam(name="order[0][dir]", required = false) String orderDirection,
		@RequestParam(name="lboAccessToken") String lboAccessToken,
		@RequestParam(name="domainName") String domainName,
		@RequestParam(name="accountCode", required = false) String accountCode,
		@RequestParam(name = "roundId", required = false) String roundId
	) throws Status425DateParseException, Status510AccountingProviderUnavailableException;

	@RequestMapping("/types")
	Response<List<TransactionType>> getAllTransactionTypes() throws Status510AccountingProviderUnavailableException;

	@RequestMapping("/account-codes")
	Response<List<AccountCode>> getAllAccountCodes() throws Status510AccountingProviderUnavailableException;

}
