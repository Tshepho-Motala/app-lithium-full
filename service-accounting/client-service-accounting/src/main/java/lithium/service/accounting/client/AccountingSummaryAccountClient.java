package lithium.service.accounting.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryAccount;

@FeignClient(name="service-accounting", path="/summary/account")
public interface AccountingSummaryAccountClient {

	@RequestMapping("/find")
	public Response<SummaryAccount> find(
			@RequestParam("periodId") Long periodId,
			@RequestParam("accountCode") String accountCode, 
			@RequestParam("accountType") String accountType, 
			@RequestParam("currencyCode") String currencyCode, 
			@RequestParam("ownerGuid") String ownerGuid);
	
	@RequestMapping("/find/granular")
	public Response<SummaryAccount> findGranular(
			@RequestParam("accountCode") String accountCode, 
			@RequestParam("accountType") String accountType, 
			@RequestParam("currencyCode") String currencyCode, 
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("granularity") Integer granularity,
			@RequestParam("offset") Integer offset);
	
	@RequestMapping("/{domain}/findLimitedByOwnerGuid")
	public Response<List<SummaryAccount>> findLimitedByOwnerGuid(
			@PathVariable("domain") String domain,
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode,  
			@RequestParam("currency") String currency, 
			@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") String dateStart,
			@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") String dateEnd);

	@RequestMapping("/{domain}/findByOwnerGuid")
	public Response<List<SummaryAccount>> findByUser(@PathVariable("domain") String domain,
													 @RequestParam("ownerGuid") String ownerGuid,
													 @RequestParam("granularity") int granularity,
													 @RequestParam("accountCode") String accountCode,
													 @RequestParam("currency") String currency);

	@RequestMapping("/{domain}/findLastByOwnerGuid")
	Response<List<SummaryAccount>> findLastByOwnerGuid(
			@PathVariable("domain") String domain,
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("last") int last,
			@RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("currency") String currency);

	@RequestMapping("/get-user-turnover")
	public Long getUserTurnoverFrom(
			@RequestParam("guid") String guid,
			@RequestParam("dateFrom") String dateFrom,
			@RequestParam("accountCodes") List<String> transactionTypes,
			@RequestParam("granularity") String granularity
	);
}
