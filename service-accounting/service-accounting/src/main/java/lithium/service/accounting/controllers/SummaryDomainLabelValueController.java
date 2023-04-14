package lithium.service.accounting.controllers;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryDomainLabelValue;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.accounting.service.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/summary/domainlabelvalue/{domain}")
public class SummaryDomainLabelValueController {
	@Autowired AccountingService accountingService;

	@RequestMapping("/find")
	public Response<List<SummaryDomainLabelValue>> find(
		@PathVariable("domain") String domain,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency,
		@RequestParam("labelName") String labelName,
		@RequestParam("labelValue") String labelValue
	) throws Exception {
		String logMsg = ("SummaryDomainLabelValue.find request : d: "+domain+", g: "+granularity+", ac: "+accountCode
			+", tt: "+transactionType+", c: "+currency+", ln: "+labelName+", lv: "+labelValue);
		log.debug(logMsg);
		return accountingService.summaryDomainLabelValueClient().find(domain, granularity, accountCode, transactionType,
			currency, labelName, labelValue);
	}

	@RequestMapping("/findLast")
	Response<List<SummaryTransactionType>> findLast3(
		@PathVariable("domain") String domain,
		@RequestParam("last") int last,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("labelName") String labelName,
		@RequestParam("labelValue") String labelValue,
		@RequestParam("currency") String currency
	) throws Exception {
		String logMsg = ("SummaryDomainLabelValue.findLast request : d: "+domain+", last: "+last+", g: "+granularity
			+", ac: "+accountCode+", tt: "+transactionType+", c: "+currency+", ln: "+labelName+", lv: "+labelValue);
		log.debug(logMsg);
		return accountingService.summaryDomainLabelValueClient().findLast(domain, last, granularity, accountCode,
			transactionType, labelName, labelValue, currency);
	}

	@RequestMapping("/findLimited")
	Response<List<SummaryLabelValue>> findLimited(
		@PathVariable("domain") String domain,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("labelName") String labelName,
		@RequestParam("labelValue") String labelValue,
		@RequestParam("currency") String currency,
		@RequestParam("dateStart") String dateStart,
		@RequestParam("dateEnd") String dateEnd
	) throws Exception {
		String logMsg = ("SummaryDomainLabelValue.findLimited request : d: "+domain+", g: "+granularity
			+", ac: "+accountCode+", tt: "+transactionType+", c: "+currency+", ln: "+labelName+", lv: "+labelValue
			+", ds: "+dateStart+", de: "+dateEnd);
		log.debug(logMsg);
		return accountingService.summaryDomainLabelValueClient().findLimited(domain, granularity, accountCode,
			transactionType, labelName, labelValue, currency, dateStart, dateEnd);
	}
}
