package lithium.service.accounting.controllers;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.accounting.service.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/summary/trantype")
public class SummaryTransactionTypeController {
	@Autowired AccountingService accountingService;

	@RequestMapping("/find")
	Response<SummaryAccountTransactionType> find(
		@RequestParam("accountId") Long accountId,
		@RequestParam("periodId") Long periodId,
		@RequestParam("transactionTypeCode") String transactionTypeCode
	) throws Exception {
		String logMsg = ("SummaryDomainTransactionType.find request : a: "+accountId+", p: "+periodId+", tt: "+transactionTypeCode);
		log.debug(logMsg);
		return accountingService.summaryTransactionTypeClient().find(accountId, periodId, transactionTypeCode);
	};
	
	@RequestMapping("/find/{accountCode}/{domainName}/{ownerGuid}/{granularity}/{currencyCode}")
	Response<SummaryAccountTransactionType> find(
		@PathVariable("accountCode") String accountCode,
		@PathVariable("domainName") String domainName,
		@PathVariable("ownerGuid") String ownerGuid,
		@PathVariable("granularity") int granularity,
		@PathVariable("currencyCode") String currencyCode
	) throws Exception {
		String logMsg = ("SummaryDomainTransactionType.find request : ac: "+accountCode+", dn: "+domainName+", guid: "+ownerGuid+", g: "+granularity+" c: "+currencyCode);
		log.debug(logMsg);
		return accountingService.summaryTransactionTypeClient().find(accountCode, domainName, ownerGuid, granularity, currencyCode);
	}
	
	@RequestMapping("/{domain}/findByOwnerGuid")
	Response<List<SummaryAccountTransactionType>> findByUser(
		@PathVariable("domain") String domain,
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency
	) throws Exception {
		String logMsg = ("SummaryDomainTransactionType.findByOwnerGuid request : d:"+domain+", guid: "+ownerGuid+", g: "+granularity+", ac: "+accountCode+", tt: "+transactionType+", c: "+currency);
		log.debug(logMsg);
		return accountingService.summaryTransactionTypeClient().findByUser(domain, ownerGuid, granularity, accountCode, transactionType, currency);
	}
	
	@RequestMapping("/{domain}/findLastByOwnerGuid")
	Response<List<SummaryTransactionType>> findLastByOwnerGuid(
		@PathVariable("domain") String domain,
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("last") int last,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency
	) throws Exception {
		String logMsg = ("SummaryDomainTransactionType.findLastByOwnerGuid request : d: "+domain+", guid: "+ownerGuid+", l: "+last+", g: "+granularity+" ac: "+accountCode+" tt: "+transactionType+" c: "+currency);
		log.debug(logMsg);
		return accountingService.summaryTransactionTypeClient().findLastByOwnerGuid(domain, ownerGuid, last, granularity, accountCode, transactionType, currency);
	}

	@RequestMapping("/{domain}/findLimitedByOwnerGuid")
	Response<List<SummaryTransactionType>> findLimitedByOwnerGuid(
		@PathVariable("domain") String domain,
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency,
		@RequestParam("dateStart") String dateStart,
		@RequestParam("dateEnd") String dateEnd
	) throws Exception {
//		@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateStart,
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.parse(dateStart);
		sdf.parse(dateEnd);
		String logMsg = ("SummaryDomainTransactionType.findLimitedByOwnerGuid request : d: "+domain+", guid: "+ownerGuid+", g: "+granularity+" ac: "+accountCode+" tt: "+transactionType+" c: "+currency+" ds: "+dateStart+" de: "+dateEnd);
		log.debug(logMsg);
		return accountingService.summaryTransactionTypeClient().findLimitedByOwnerGuid(domain, ownerGuid, granularity, accountCode, transactionType, currency, dateStart, dateEnd);
	}

    @RequestMapping("/{domain}/findTypesByOwnerGuid")
    Response<List<SummaryAccountTransactionType>> findTypesByOwnerGuid(
            @PathVariable("domain") String domain,
            @RequestParam("ownerGuid") String ownerGuid,
            @RequestParam("granularity") int granularity,
            @RequestParam("accountCode") String accountCode,
            @RequestParam("transactionTypes") List<String> transactionTypes
    ) throws Exception {
         return accountingService.summaryTransactionTypeClient().findTypesByOwnerGuid(domain, ownerGuid, granularity, accountCode, transactionTypes);
    }
}
