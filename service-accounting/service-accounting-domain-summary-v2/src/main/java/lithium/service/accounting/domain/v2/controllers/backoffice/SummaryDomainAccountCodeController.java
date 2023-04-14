package lithium.service.accounting.domain.v2.controllers.backoffice;

import lithium.service.Response;
import lithium.service.accounting.domain.v2.services.SummaryDomainService;
import lithium.service.accounting.domain.v2.storage.entities.SummaryDomain;
import lithium.service.accounting.objects.SummaryDomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/backoffice/summary/domain-account-code/{domainName}")
public class SummaryDomainAccountCodeController {
	@Autowired private SummaryDomainService service;

	@RequestMapping("/find")
	public Response<List<SummaryDomain>> find(@PathVariable("domainName") String domainName,
			  @RequestParam(name = "testUsers", required = false) Boolean testUsers,
			  @RequestParam("granularity") int granularity, @RequestParam("accountCode") String accountCode,
		      @RequestParam("currency") String currency) {

		List<SummaryDomain> result = service.find(domainName, testUsers, granularity, accountCode, currency);
		return Response.<List<SummaryDomain>>builder()
				.data(result)
				.status(Response.Status.OK)
				.build();
	}

	@RequestMapping("/find-last")
	public Response<List<SummaryDomainType>> findLast(@PathVariable("domainName") String domain,
	  		@RequestParam(name = "testUsers", required = false) Boolean testUsers,@RequestParam("last") int last, @RequestParam("granularity") int granularity,
	        @RequestParam("accountCode") String accountCode, @RequestParam("currency") String currency) {

		List<SummaryDomainType> result = service.findLast(domain, testUsers, last, granularity, accountCode, currency);

		return Response.<List<SummaryDomainType>>builder()
				.data(result)
				.status(Response.Status.OK)
				.build();
	}

	@RequestMapping("/find-limited")
	public Response<List<SummaryDomainType>> findLimited(@PathVariable("domainName") String domainName,
		 	@RequestParam(name = "testUsers", required = false) Boolean testUsers,
		 	@RequestParam("granularity") int granularity, @RequestParam("accountCode") String accountCode,
			@RequestParam("currency") String currency,
			@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateStart,
			@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateEnd) {

		List<SummaryDomainType> result = service.findLimited(domainName, testUsers, granularity, accountCode, currency, dateStart,
					dateEnd);

			return Response.<List<SummaryDomainType>>builder()
				.data(result)
				.status(Response.Status.OK)
				.build();
	}
}
