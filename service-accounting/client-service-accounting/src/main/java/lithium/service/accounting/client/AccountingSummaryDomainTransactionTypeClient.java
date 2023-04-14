package lithium.service.accounting.client;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryDomainTransactionType;
import lithium.service.accounting.objects.SummaryTransactionType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@FeignClient(name="service-accounting", path="/summary/domaintrantype")
public interface AccountingSummaryDomainTransactionTypeClient {
	
	@RequestMapping("/{domain}/find")
	Response<List<SummaryDomainTransactionType>> find(@PathVariable("domain") String domain, @RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode, @RequestParam("transactionType") String transactionType, @RequestParam("currency") String currency);
	
	@RequestMapping("/{domain}/findLast")
	Response<List<SummaryTransactionType>> findLast3(
			@PathVariable("domain") String domain, 
			@RequestParam("last") int last,
			@RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode, 
			@RequestParam("transactionType") String transactionType, 
			@RequestParam("currency") String currency);

	@RequestMapping("/findLimited")
	Response<List<SummaryTransactionType>> findLimited(
			@PathVariable("domain") String domain, 
			@RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode, 
			@RequestParam("transactionType") String transactionType, 
			@RequestParam("currency") String currency, 
			@RequestParam("dateStart") String dateStart,
			@RequestParam("dateEnd") String dateEnd);
}
