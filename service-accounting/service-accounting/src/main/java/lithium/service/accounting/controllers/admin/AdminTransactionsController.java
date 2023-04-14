package lithium.service.accounting.controllers.admin;

import lithium.exceptions.Status425DateParseException;
import lithium.service.Response;
import lithium.service.accounting.client.AdminTransactionsClient;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.accounting.objects.TransactionType;
import lithium.service.accounting.service.AccountingService;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/transactions")
public class AdminTransactionsController /*implements AdminTransactionsClient*/ {
	@Autowired private AccountingService service;

//	@Override
	@RequestMapping(value = "/table", method = RequestMethod.POST)
	public DataTableResponse<TransactionEntryBO> table(
		@RequestParam(name="dateRangeStart", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") String dateRangeStart,
		@RequestParam(name="dateRangeEnd", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") String dateRangeEnd,
		@RequestParam(name="userGuid", required=false) String userGuid,
		@RequestParam(name="transactionId", required=false) String transactionId,
		@RequestParam(name="draw") String draw,
		@RequestParam(name="start") int start,
		@RequestParam(name="length") int length,
		@RequestParam(name="search", required=false) String searchValue,
		@RequestParam(name="providerGuid", required=false) String providerGuid,
		@RequestParam(name="providerTransId", required=false) String providerTransId,
		@RequestParam(name="transactionType", required=false) List<String> transactionType,
		@RequestParam(name="additionalTransId", required = false) String additionalTransId,
		@RequestParam(name="order[0][dir]", required = false) String orderDirection,
		@RequestParam(name="domainName") String domainName,
		@RequestParam(name="accountCode", required = false) String accountCode,
		@RequestParam(name ="roundId", required = false) String roundId,
		LithiumTokenUtil tokenUtil
	) throws Status510AccountingProviderUnavailableException, Status425DateParseException {
		String logMsg = ("AdminTransactions.table request : drs: "+dateRangeStart+", dre: "+dateRangeEnd
			+", guid: "+userGuid+", tid: "+transactionId);
		log.debug(logMsg);
		return service.adminTransactionsClient().table(dateRangeStart, dateRangeEnd, userGuid,
				transactionId, draw, start, length, searchValue,providerGuid, providerTransId, transactionType, additionalTransId,
				orderDirection, tokenUtil.getAccessToken().getValue(), domainName, accountCode, roundId);

	}

//	@Override
	@GetMapping("/types")
	public Response<List<TransactionType>> getAllTransactionTypes() throws Status510AccountingProviderUnavailableException {
		return service.adminTransactionsClient().getAllTransactionTypes();
	}

}
