package lithium.service.accounting.controllers;

import lithium.service.Response;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.SummaryAccountLabelValue;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.accounting.objects.SummaryLabelValueTotal;
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
@RequestMapping("/summary/accountlabelvalue")
public class SummaryAccountLabelValueController {
	@Autowired AccountingService accountingService;

	@RequestMapping("/find")
	public Response<List<SummaryAccountLabelValue>> find(@RequestParam("domainName") String domainName,
			@RequestParam("periodId") Long periodId, @RequestParam("accountCode") String accountCode,
			@RequestParam("transactionTypeCode") String transactionTypeCode,
			@RequestParam("labelValue") String labelValue, @RequestParam("labelName") String labelName,
			@RequestParam("currencyCode") String currencyCode) throws Exception {
		return accountingService.summaryAccountLabelValueClient().find(domainName, periodId, accountCode,
				transactionTypeCode, labelValue, labelName, currencyCode);
	}

	@RequestMapping("/find-multiple-tran-types")
	public Response<List<SummaryAccountLabelValue>> find(@RequestParam("domainName") String domainName,
		     @RequestParam("periodId") Long periodId, @RequestParam("accountCode") String accountCode,
		     @RequestParam("transactionTypeCodes") List<String> transactionTypeCodes,
		     @RequestParam("labelValue") String labelValue, @RequestParam("labelName") String labelName,
		     @RequestParam("currencyCode") String currencyCode) throws Exception {
		return accountingService.summaryAccountLabelValueClient().find(domainName, periodId, accountCode,
				transactionTypeCodes, labelValue, labelName, currencyCode);
	}

	@RequestMapping("/{domain}/findLimited")
	public Response<List<SummaryLabelValue>> findLimited(@PathVariable("domain") String domain,
             @RequestParam("granularity") int granularity, @RequestParam("accountCode") String accountCode,
             @RequestParam("transactionType") String transactionType, @RequestParam("labelName") String labelName,
             @RequestParam("labelValue") String labelValue, @RequestParam("currency") String currency,
             @RequestParam("dateStart") String dateStart, @RequestParam("dateEnd") String dateEnd,
             @RequestParam("ownerGuid") String ownerGuid) throws Status510AccountingProviderUnavailableException {
		return accountingService.summaryAccountLabelValueClient().findLimited(domain, granularity, accountCode,
				transactionType, labelName, labelValue, currency, dateStart, dateEnd, ownerGuid);
	}

	@RequestMapping("/{domain}/find-summary-label-value-total")
	public Response<List<SummaryLabelValueTotal>> findSummaryLabelValueTotal(@PathVariable("domain") String domain,
             @RequestParam("labelName") String labelName, @RequestParam("labelValues") List<String> labelValues,
             @RequestParam("userGuid") String userGuid) throws Status510AccountingProviderUnavailableException {
		return accountingService.summaryAccountLabelValueClient().findSummaryLabelValueTotal(domain, labelName,
				labelValues, userGuid);
	}
}
