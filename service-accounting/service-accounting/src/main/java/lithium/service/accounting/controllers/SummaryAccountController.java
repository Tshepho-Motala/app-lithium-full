package lithium.service.accounting.controllers;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryAccount;
import lithium.service.accounting.service.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/summary/account")
public class SummaryAccountController {
	@Autowired AccountingService accountingService;

	@RequestMapping("/find")
	public Response<SummaryAccount> find(@RequestParam("periodId") Long periodId,
										 @RequestParam("accountCode") String accountCode,
										 @RequestParam("accountType") String accountType,
										 @RequestParam("currencyCode") String currencyCode,
										 @RequestParam("ownerGuid") String ownerGuid) throws Exception {
		log.debug("SummaryAccountController.find [periodId="+periodId+", accountCode="+accountCode
			+ ", accountType="+accountType+", currencyCode="+currencyCode+", ownerGuid="+ownerGuid+"]");
		return accountingService.summaryAccountClient().find(periodId, accountCode, accountType, currencyCode,
			ownerGuid);
	}

	@RequestMapping("/find/granular")
	public Response<SummaryAccount> findGranular(@RequestParam("accountCode") String accountCode,
												 @RequestParam("accountType") String accountType,
												 @RequestParam("currencyCode") String currencyCode,
												 @RequestParam("ownerGuid") String ownerGuid,
												 @RequestParam("granularity") Integer granularity,
												 @RequestParam("offset") Integer offset) throws Exception {
		log.debug("SummaryAccountController.findGranular [accountCode="+accountCode+", accountType="+accountType
			+ ", currencyCode="+currencyCode+", ownerGuid="+ownerGuid+", granularity="+granularity
			+ ", offset="+offset+"]");
		return accountingService.summaryAccountClient().findGranular(accountCode, accountType, currencyCode, ownerGuid,
			granularity, offset);
	}

	@RequestMapping("/{domain}/findLimitedByOwnerGuid")
	public Response<List<SummaryAccount>> findLimitedByOwnerGuid(
			@PathVariable("domain") String domain,
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("currency") String currency,
			@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") String dateStart,
			@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") String dateEnd) throws Exception {
		log.debug("SummaryAccountController.findLimitedByOwnerGuid [domain="+domain+", ownerGuid="+ownerGuid
			+ ", granularity="+granularity+", accountCode="+accountCode+", currency="+currency
			+ ", dateStart="+dateStart+", dateEnd="+dateEnd+"]");
		return accountingService.summaryAccountClient().findLimitedByOwnerGuid(domain, ownerGuid, granularity,
			accountCode, currency, dateStart, dateEnd);
	}

	@RequestMapping("/{domain}/findByOwnerGuid")
	public Response<List<SummaryAccount>> findByUser(@PathVariable("domain") String domain,
													 @RequestParam("ownerGuid") String ownerGuid,
													 @RequestParam("granularity") int granularity,
													 @RequestParam("accountCode") String accountCode,
													 @RequestParam("currency") String currency) throws Exception {
		log.debug("SummaryAccountController.findByUser [domain="+domain+", ownerGuid="+ownerGuid
			+ ", granularity="+granularity+", accountCode="+accountCode+", currency="+currency+"]");
		return accountingService.summaryAccountClient().findByUser(domain, ownerGuid, granularity, accountCode,
			currency);
	}

	@RequestMapping("/{domain}/findLastByOwnerGuid")
	public Response<List<SummaryAccount>> findLastByOwnerGuid(@PathVariable("domain") String domain,
															  @RequestParam("ownerGuid") String ownerGuid,
															  @RequestParam("last") int last,
															  @RequestParam("granularity") int granularity,
															  @RequestParam("accountCode") String accountCode,
															  @RequestParam("currency") String currency)
			throws Exception {
		log.debug("SummaryAccountController.findLastByOwnerGuid [domain="+domain+", ownerGuid="+ownerGuid
			+ ", last="+last+", granularity="+granularity+", accountCode="+accountCode+", currency="+currency+"]");
		return accountingService.summaryAccountClient().findLastByOwnerGuid(domain, ownerGuid, last, granularity,
			accountCode, currency);
	}

	@RequestMapping("/get-user-turnover")
	public Long getUserTurnoverFrom(
			@RequestParam("guid") String guid,
			@RequestParam("dateFrom") String dateFrom,
			@RequestParam("accountCodes") List<String> accountCodes,
			@RequestParam("granularity") String granularity
	) throws Exception {
		return accountingService.summaryAccountClient().getUserTurnoverFrom(guid, dateFrom, accountCodes, granularity);
	}
}
