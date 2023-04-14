package lithium.service.accounting.client;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryAccountLabelValueType;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.accounting.objects.SummaryTransactionType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-accounting")
public interface AccountingSummaryTransactionTypeClient {

	@RequestMapping("/summary/trantype/find")
	Response<SummaryAccountTransactionType> find(
		@RequestParam("accountId") Long accountId,
		@RequestParam("periodId") Long periodId,
		@RequestParam("transactionTypeCode") String transactionTypeCode
	) throws Exception;

	@RequestMapping("/summary/trantype/find/{accountCode}/{domainName}/{ownerGuid}/{granularity}/{currencyCode}")
	Response<SummaryAccountTransactionType> find(
		@PathVariable("accountCode") String accountCode,
		@PathVariable("domainName") String domainName,
		@PathVariable("ownerGuid") String ownerGuid,
		@PathVariable("granularity") int granularity,
		@PathVariable("currencyCode") String currencyCode
	) throws Exception;

	@RequestMapping("/summary/trantype/{domain}/findByOwnerGuid")
	Response<List<SummaryAccountTransactionType>> findByUser(
		@PathVariable("domain") String domain,
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency
	) throws Exception;

	@RequestMapping("/summary/trantype/{domain}/findLastByOwnerGuid")
	Response<List<SummaryTransactionType>> findLastByOwnerGuid(
		@PathVariable("domain") String domain,
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("last") int last,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency
	) throws Exception;

	@RequestMapping("/summary/trantype/{domain}/findLimitedByOwnerGuid")
	Response<List<SummaryTransactionType>> findLimitedByOwnerGuid(
		@PathVariable("domain") String domain,
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency,
		@RequestParam("dateStart") String dateStart,
		@RequestParam("dateEnd") String dateEnd
	) throws Exception;
	
	@RequestMapping("/summary/accountlabelvalue/find/{granularity}/{transactionTypeCode}/{accountCode}/{ownerGuid}/{domainName}/{currencyCode}/{labelValue}/{labelName}")
	Response<SummaryAccountLabelValueType> summaryAccountLabelValueType(
		@PathVariable("granularity") int granularity,
		@PathVariable("transactionTypeCode") String transactionTypeCode,
		@PathVariable("accountCode") String accountCode,
		@PathVariable("ownerGuid") String ownerGuid,
		@PathVariable("domainName") String domainName,
		@PathVariable("currencyCode") String currencyCode,
		@PathVariable("labelValue") String labelValue,
		@PathVariable("labelName") String labelName
	) throws Exception;

    @RequestMapping("/summary/trantype/{domain}/findTypesByOwnerGuid")
    Response<List<SummaryAccountTransactionType>> findTypesByOwnerGuid(
            @PathVariable("domain") String domain,
            @RequestParam("ownerGuid") String ownerGuid,
            @RequestParam("granularity") int granularity,
            @RequestParam("accountCode") String accountCode,
            @RequestParam("transactionTypes") List<String> transactionTypes
    ) ;
}