package lithium.service.accounting.domain.summary.controllers.backoffice;

import lithium.service.Response;
import lithium.service.accounting.domain.summary.services.SummaryDomainTransactionTypeService;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomainTransactionType;
import lithium.service.accounting.objects.SummaryTransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/backoffice/summary/domain-tran-type/{domainName}")
public class SummaryDomainTransactionTypeController {
	@Autowired private SummaryDomainTransactionTypeService service;

	@RequestMapping("/find")
	public Response<List<SummaryDomainTransactionType>> find(@PathVariable("domainName") String domainName,
			@RequestParam int granularity, @RequestParam String accountCode, @RequestParam String transactionType,
			@RequestParam String currency) {
		List<SummaryDomainTransactionType> result = service.find(domainName, granularity, accountCode, transactionType,
				currency);
		return Response.<List<SummaryDomainTransactionType>>builder()
				.data(result)
				.status(Response.Status.OK)
				.build();
	}

	@RequestMapping("/find-last")
	public Response<List<SummaryTransactionType>> findLast(@PathVariable("domainName") String domainName,
			@RequestParam("last") int last, @RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode, @RequestParam("transactionType") String transactionType,
			@RequestParam("currency") String currency) {
		List<SummaryTransactionType> result = service.findLast(domainName, last, granularity, accountCode,
				transactionType, currency);
		return Response.<List<SummaryTransactionType>>builder()
				.data(result)
				.status(Response.Status.OK)
				.build();
	}

	@RequestMapping("/find-limited")
	public Response<List<SummaryTransactionType>> findLimited(@PathVariable("domainName") String domainName,
			@RequestParam("granularity") int granularity, @RequestParam("accountCode") String accountCode,
			@RequestParam("transactionType") String transactionType, @RequestParam("currency") String currency,
			@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateStart,
			@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateEnd) {
		List<SummaryTransactionType> result = service.findLimited(domainName, granularity, accountCode, transactionType,
				currency, dateStart, dateEnd);
		return Response.<List<SummaryTransactionType>>builder()
				.data(result)
				.status(Response.Status.OK)
				.build();
	}
}
