package lithium.service.accounting.client;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryDomainLabelValue;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.accounting.objects.SummaryTransactionType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name="service-accounting-provider-internal", path = "/summary/domainlabelvalue")
public interface AccountingSummaryDomainLabelValueClient {

	@RequestMapping("/{domain}/find")
	Response<List<SummaryDomainLabelValue>> find(
		@PathVariable("domain") String domain,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency,
		@RequestParam("labelName") String labelName,
		@RequestParam("labelValue") String labelValue);

	@RequestMapping("/{domain}/findLast")
	Response<List<SummaryTransactionType>> findLast(
		@PathVariable("domain") String domain,
		@RequestParam("last") int last,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("labelName") String labelName,
		@RequestParam("labelValue") String labelValue,
		@RequestParam("currency") String currency);

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
		@RequestParam("dateEnd") String dateEnd);

}
