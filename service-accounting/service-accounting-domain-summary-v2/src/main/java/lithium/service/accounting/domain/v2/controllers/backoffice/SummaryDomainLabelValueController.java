package lithium.service.accounting.domain.v2.controllers.backoffice;

import lithium.service.Response;
import lithium.service.accounting.domain.v2.services.SummaryDomainLabelValueService;
import lithium.service.accounting.domain.v2.storage.entities.SummaryDomainLabelValue;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.accounting.objects.SummaryTransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backoffice/summary/domain-label-value/{domainName}")
public class SummaryDomainLabelValueController {
	@Autowired private SummaryDomainLabelValueService service;

	@RequestMapping("/find")
	public Response<List<SummaryDomainLabelValue>> find(@PathVariable("domainName") String domainName,
			@RequestParam(name = "testUsers", required = false) Boolean testUsers,
			@RequestParam("granularity") int granularity, @RequestParam("accountCode") String accountCode,
	        @RequestParam("transactionType") String transactionType, @RequestParam("currency") String currency,
	        @RequestParam("labelName") String labelName, @RequestParam("labelValue") String labelValue) {

		List<SummaryDomainLabelValue> result = service.find(domainName, testUsers, granularity, accountCode, transactionType,
					currency, labelName, labelValue);

		return Response.<List<SummaryDomainLabelValue>>builder()
				.data(result)
				.status(Response.Status.OK)
				.build();
	}

	@RequestMapping("/find-last")
	public Response<List<SummaryTransactionType>> findLast(@PathVariable("domainName") String domainName,
		   	@RequestParam(name = "testUsers", required = false) Boolean testUsers,
		   	@RequestParam("last") int last, @RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode, @RequestParam("transactionType") String transactionType,
			@RequestParam("labelName") String labelName, @RequestParam("labelValue") String labelValue,
			@RequestParam("currency") String currency) {

		List<SummaryTransactionType> result = service.findLast(domainName, testUsers, last, granularity, accountCode,
					transactionType, labelName, labelValue, currency);

		return Response.<List<SummaryTransactionType>>builder()
				.data(result)
				.status(Response.Status.OK)
				.build();
	}

	@RequestMapping("/find-limited")
	public Response<List<SummaryLabelValue>> findLimited(@PathVariable("domainName") String domainName,
		 	@RequestParam(name = "testUsers", required = false) Boolean testUsers,
	 		@RequestParam("granularity") int granularity, @RequestParam("accountCode") String accountCode,
			@RequestParam("transactionType") String transactionType, @RequestParam("labelName") String labelName,
			@RequestParam("labelValue") String labelValue, @RequestParam("currency") String currency,
			@RequestParam("dateStart") String dateStart, @RequestParam("dateEnd") String dateEnd) {

		List<SummaryLabelValue> result = service.findLimited(domainName, testUsers, granularity, accountCode, transactionType,
					labelName, labelValue, currency, dateStart, dateEnd);

		return Response.<List<SummaryLabelValue>>builder()
				.data(result)
				.status(Response.Status.OK)
				.build();
	}
}
