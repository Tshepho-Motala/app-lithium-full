package lithium.service.accounting.provider.internal.controllers.admin;

import lithium.exceptions.Status425DateParseException;
import lithium.service.Response;
import lithium.service.accounting.client.AdminTransactionsClient;
import lithium.service.accounting.objects.AccountCode;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.accounting.objects.TransactionType;
import lithium.service.accounting.provider.internal.services.AccountCodeService;
import lithium.service.accounting.provider.internal.services.TransactionEntryService;
import lithium.service.accounting.provider.internal.services.TransactionTypeService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin/transactions")
public class AdminTransactionsController implements AdminTransactionsClient {
	@Autowired private TokenStore tokenStore;
	@Autowired private TransactionEntryService service;
	@Autowired private AccountCodeService accountCodeService;
	@Autowired private TransactionTypeService transactionTypeService;

	@Override
	@RequestMapping(value = "/table")
	public DataTableResponse<TransactionEntryBO> table(
		@RequestParam(name="dateRangeStart", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") String dateRangeStart,
		@RequestParam(name="dateRangeEnd", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") String dateRangeEnd,
		@RequestParam(name="userGuid", required=false) String userGuid,
		@RequestParam(name="transactionId", required=false) String transactionId,
		@RequestParam(name="draw") String draw,
		@RequestParam(name="start") int start,
		@RequestParam(name="length") int length,
		@RequestParam(name="search", required=false) String searchValue,
		@RequestParam(name="providerTransId", required=false) String providerTransId,
		@RequestParam(name="providerGuid", required=false) String providerGuid,
		@RequestParam(name="transactionType", required=false) List<String> transactionType,
		@RequestParam(name="additionalTransId", required = false) String additionalTransId,
		@RequestParam(name="order[0][dir]", required = false) String orderDirection,
		@RequestParam(name="lboAccessToken") String lboAccessToken,
		@RequestParam(name="domainName") String domainName,
		@RequestParam(name="accountCode", required = false) String accountCode,
		@RequestParam(name="roundId", required = false) String roundId
	) throws Status425DateParseException {
		// This is necessary in order to filter the accounting datatables in LBO by player domains for the BO user.
		List<String> domains = new ArrayList<>();

		//Make sure we are querying for the right domain
		if(domainName != null && ! domainName.isEmpty()) {
			domains.add(domainName);
		} else {
			LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, lboAccessToken).build();
			log.debug("tokenUtil.guid:: " + tokenUtil.guid());
			domains = tokenUtil.playerDomainsWithRoles(
					"PLAYER_ACCOUNTING_SPORTSBOOK_HISTORY_VIEW",
					"PLAYER_ACCOUNTING_HISTORY_VIEW",
					"GLOBAL_ACCOUNTING_VIEW")
					.stream()
					.map(jwtDomain -> jwtDomain.getName())
					.collect(Collectors.toList());
		}

		log.debug("Transactions table requested [domains="+domains.stream().collect(Collectors.joining(","))
			+", dateRangeStart="+dateRangeStart+", dateRangeEnd="+dateRangeStart+", userGuid="+userGuid
			+", transactionId="+transactionId+", draw="+draw+", start="+start+", length="+length+", search="+searchValue
			+", providerTransId="+providerTransId+", providerGuid="+providerGuid+", transactionType="+transactionType+"]");
		if (length > 100) length = 100;

		if (orderDirection == null) {
			orderDirection = "desc";
		}

		PageRequest pageRequest = PageRequest.of(start/length, length, Sort.Direction.fromString(orderDirection), new String[] {"id"});
		DataTableRequest request = new DataTableRequest();
		request.setPageRequest(pageRequest);

		Page<TransactionEntryBO> table = service.find(domains, dateRangeStart, dateRangeEnd, userGuid, transactionId,
			searchValue, providerGuid, providerTransId, transactionType, additionalTransId, pageRequest, accountCode, roundId);
		return new DataTableResponse<>(request, table);
	}

	@Override
	@GetMapping("/types")
	public Response<List<TransactionType>> getAllTransactionTypes() {
		List<TransactionType> types = transactionTypeService.findAll();
		return Response.<List<TransactionType>>builder().data(types).status(Response.Status.OK).build();
	}

	@Override
	@GetMapping("/account-codes")
	public Response<List<AccountCode>> getAllAccountCodes() {
		List<AccountCode> accountCodes = accountCodeService.findAllCodes();
		return Response.<List<AccountCode>>builder().data(accountCodes).status(Response.Status.OK).build();
	}

}
