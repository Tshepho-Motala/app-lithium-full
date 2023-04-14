package lithium.service.accounting.controllers;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryDomainTransactionType;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.accounting.service.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/summary/domaintrantype/{domain}")
public class SummaryDomainTransactionTypeController {
	@Autowired AccountingService accountingService;

	@RequestMapping("/find")
	Response<List<SummaryDomainTransactionType>> find(
		@PathVariable("domain") String domain,
		@RequestParam Integer granularity,
		@RequestParam String accountCode,
		@RequestParam String transactionType,
		@RequestParam String currency
	) throws Exception {
		String logMsg = ("SummaryDomainTransactionType.find request : d: "+domain+", g: "+granularity+", tt: "+transactionType+", c: "+currency);
		log.debug(logMsg);
		return accountingService.summaryDomainTransactionTypeClient().find(domain, granularity, accountCode, transactionType, currency);
	}

	@RequestMapping("/findLast")
	Response<List<SummaryTransactionType>> findLast3(
		@PathVariable("domain") String domain,
		@RequestParam("last") Integer last,
		@RequestParam("granularity") Integer granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency
	) throws Exception {
		String logMsg = ("SummaryDomainTransactionType.findLast3 request : d: "+domain+", l:"+last+ ", g: "+granularity+", ac:"+accountCode+", tt: "+transactionType+", c: "+currency);
		log.debug(logMsg);
		return accountingService.summaryDomainTransactionTypeClient().findLast3(domain, last, granularity, accountCode, transactionType, currency);
	}

	@RequestMapping("/findLimited")
	Response<List<SummaryTransactionType>> findLimited(
		@PathVariable("domain") String domain,
		@RequestParam("granularity") Integer granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency,
		@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateStart,
		@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateEnd
	) throws Exception {
		String logMsg = ("SummaryDomainTransactionType.findLimited request : d: "+domain+", g: "+granularity+", ac: "+accountCode+", tt: "+transactionType+", c: "+currency+", ds: "+dateStart+", de: "+dateEnd);
		log.debug(logMsg);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return accountingService.summaryDomainTransactionTypeClient().findLimited(domain, granularity, accountCode, transactionType, currency, df.format(dateStart), df.format(dateEnd));
	}
}
