package lithium.service.accounting.client;

import java.util.List;

import lithium.service.accounting.objects.SummaryLabelValueTotal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryAccountLabelValue;
import lithium.service.accounting.objects.SummaryLabelValue;


@FeignClient(name="service-accounting", path="/summary/accountlabelvalue")
public interface AccountingSummaryAccountLabelValueClient {
	
	@RequestMapping("/find")
	Response<List<SummaryAccountLabelValue>> find(
		@RequestParam("domainName") String domainName,
		@RequestParam("periodId") Long periodId,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionTypeCode") String transactionTypeCode,
		@RequestParam("labelValue") String labelValue,
		@RequestParam("labelName") String labelName,
		@RequestParam("currencyCode") String currencyCode
	) throws Exception;

	@RequestMapping("/find-multiple-tran-types")
	Response<List<SummaryAccountLabelValue>> find(
			@RequestParam("domainName") String domainName,
			@RequestParam("periodId") Long periodId,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("transactionTypeCodes") List<String> transactionTypeCodes,
			@RequestParam("labelValue") String labelValue,
			@RequestParam("labelName") String labelName,
			@RequestParam("currencyCode") String currencyCode
	) throws Exception;
	
	@RequestMapping("/{domain}/findLimited")
	Response<List<SummaryLabelValue>> findLimited(
			@PathVariable("domain") String domain, 
			@RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode, 
			@RequestParam("transactionType") String transactionType, 
			@RequestParam("labelName") String labelName,
			@RequestParam("labelValue") String labelValue,
			@RequestParam("currency") String currency, 
			@RequestParam("dateStart") String dateStart,
			@RequestParam("dateEnd") String dateEnd,
			@RequestParam("ownerGuid") String ownerGuid);

	@RequestMapping("/{domain}/find-summary-label-value-total")
	Response<List<SummaryLabelValueTotal>> findSummaryLabelValueTotal(
			@PathVariable("domain") String domain,
			@RequestParam("labelName") String labelName,
			@RequestParam("labelValues") List<String> labelValues,
			@RequestParam("userGuid") String userGuid);

}
