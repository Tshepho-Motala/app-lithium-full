package lithium.service.accounting.provider.internal.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccount;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.specifications.SummaryAccountSpecifications;
import lithium.service.accounting.provider.internal.services.DomainService;
import lithium.service.accounting.provider.internal.services.PeriodService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/summary/account")
@Slf4j
public class SummaryAccountController {
	@Autowired DomainService domainService;
	@Autowired PeriodService periodService;
	@Autowired SummaryAccountRepository repo;

	@RequestMapping("/find")
	public Response<SummaryAccount> find(
			@RequestParam Long periodId,
			@RequestParam String accountCode, 
			@RequestParam String accountType, 
			@RequestParam String currencyCode, 
			@RequestParam String ownerGuid) {
		return Response.<SummaryAccount>builder().data(
			repo.findByPeriodIdAndAccountAccountCodeCodeAndAccountAccountTypeCodeAndAccountOwnerGuidAndAccountCurrencyCode(
					periodId, accountCode, accountType, ownerGuid, currencyCode)).status(Status.OK).build();
	}
	
	@RequestMapping("/find/granular")
	public Response<SummaryAccount> findGranular(
			@RequestParam String accountCode,
			@RequestParam String accountType,
			@RequestParam String currencyCode,
			@RequestParam String ownerGuid,
			@RequestParam("granularity") Integer granularity,
			@RequestParam("offset") Integer offset
	) {
		Period period = periodService.findOrCreatePeriodByOffset(offset, domainService.findOrCreate(ownerGuid.split("/")[0]), granularity);
		return Response.<SummaryAccount>builder().data(
			repo.findByPeriodIdAndAccountAccountCodeCodeAndAccountAccountTypeCodeAndAccountOwnerGuidAndAccountCurrencyCode(
					period.getId(), accountCode, accountType, ownerGuid, currencyCode)).status(Status.OK).build();
	}
	
	@RequestMapping("/find/{accountCode}/{domainName}/{ownerGuid}/{granularity}/{currencyCode}")
	Response<SummaryAccount> find(
		@PathVariable("accountCode") String accountCode,
		@PathVariable("domainName") String domainName,
		@PathVariable("ownerGuid") String ownerGuid,
		@PathVariable("granularity") int granularity,
		@PathVariable("currencyCode") String currencyCode
	) throws Exception {
		return Response.<SummaryAccount>builder().data(
			repo.findByAccountAccountCodeCodeAndAccountOwnerGuidAndAccountCurrencyCodeAndPeriodGranularityAndPeriodDomainName(
				accountCode,
				URLDecoder.decode(ownerGuid, "UTF-8"),
				domainName,
				granularity,
				domainName
			)
		).build();
	}
	
	@RequestMapping("/{domain}/findByOwnerGuid")
	Response<List<SummaryAccount>> findByUser(
		@PathVariable("domain") String domain,
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("currency") String currency
	) throws Exception {
		return Response.<List<SummaryAccount>>builder().data(
			repo.findByAccountOwnerGuidAndPeriodGranularityAndPeriodDomainNameAndAccountAccountCodeCodeAndAccountCurrencyCodeOrderByPeriodDateStart(
				ownerGuid, granularity, domain, accountCode, currency
			)
		).build();
	}
	
	@RequestMapping("/{domain}/findLastByOwnerGuid")
	Response<List<lithium.service.accounting.objects.SummaryAccount>> findLastByUser(
			@PathVariable("domain") String domain,
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("last") int last,
			@RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("currency") String currency) {
		
		ArrayList<lithium.service.accounting.objects.SummaryAccount> results = new ArrayList<>();
		
		DateTime now = new DateTime();
		ArrayList<DateTime> dateStart = new ArrayList<>();
		ArrayList<DateTime> dateEnd = new ArrayList<>();
		
		if (granularity == 1) {
			DateTime date = new DateTime(now.getYear(), 1, 1, 0, 0);
			dateStart.add(date);
			dateEnd.add(date.plusYears(1));
			for (int i = 0; i < last; i ++) {
				dateStart.add(date.minusYears(i+1));
				dateEnd.add(date.minusYears(i+1).plusYears(1));
			}
		} else if (granularity == 2) {
			DateTime date = new DateTime(now.getYear(), now.getMonthOfYear(), 1, 0, 0);
			dateStart.add(date);
			dateEnd.add(date.plusMonths(1));
			for (int i = 0; i < last; i ++) {
				dateStart.add(date.minusMonths(i+1));
				dateEnd.add(date.minusMonths(i+1).plusMonths(1));
			}
		} else if (granularity == 3) {
			DateTime date = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
			dateStart.add(date);
			dateEnd.add(date.plusDays(1));
			for (int i = 0; i < last; i ++) {
				dateStart.add(date.minusDays(i+1));
				dateEnd.add(date.minusDays(i+1).plusDays(1));
			}
		} else if (granularity == 4) {
			DateTime date = new DateTime(now.getYear(), 1, 1, 0, 0).withWeekyear(now.getWeekyear()).withWeekOfWeekyear(now.getWeekOfWeekyear()).withDayOfWeek(1);
			dateStart.add(date);
			dateEnd.add(date.plusWeeks(1));
			for (int i = 0; i < last; i ++) {
				dateStart.add(date.minusWeeks(i+1));
				dateEnd.add(date.minusWeeks(i+1).plusWeeks(1));
			}
		} else throw new RuntimeException("Invalid granularity: " + granularity);

		for (int i = 0; i < last + 1; i ++) {
			Specification<SummaryAccount> spec = Specification.where(
					SummaryAccountSpecifications.find(
							domain, 
							currency, 
							accountCode,
							granularity,
							dateStart.get(i).toDate(), 
							dateEnd.get(i).toDate(),
							ownerGuid)
					);
			
			SummaryAccount tran = repo.findOne(spec).orElse(null);
			if (tran != null) {
				results.add(lithium.service.accounting.objects.SummaryAccount.builder()
						.tranCount(tran.getTranCount())
						.debitCents(tran.getDebitCents())
						.creditCents(tran.getCreditCents())
						.openingBalanceCents(tran.getOpeningBalanceCents())
						.closingBalanceCents(tran.getClosingBalanceCents())
						.dateStart(tran.getPeriod().getDateStart())
						.dateEnd(tran.getPeriod().getDateEnd())
					.build());
			} else {
				//Find the first available one since we could not find one in the specific range
				tran = repo.findFirstByAccountOwnerGuidAndPeriodGranularityAndPeriodDomainNameAndAccountAccountCodeCodeAndAccountCurrencyCodeAndPeriodDateStartBeforeOrderByPeriodDateStartDesc(ownerGuid, granularity, domain, accountCode, currency, dateStart.get(i).toDate());
				if (tran != null) {
					//Use closing balance for opening balance as well and use the original date search range filters for the summary (Now it should work as if the balance never changed)
					results.add(lithium.service.accounting.objects.SummaryAccount.builder()
							.tranCount(0L)
							.debitCents(tran.getClosingBalanceCents() < 0 ? tran.getClosingBalanceCents() : 0L )
							.creditCents(tran.getClosingBalanceCents() > 0 ? tran.getClosingBalanceCents() : 0L )
							.openingBalanceCents(tran.getClosingBalanceCents())
							.closingBalanceCents(tran.getClosingBalanceCents())
							.dateStart(dateStart.get(i).toDate())
							.dateEnd(dateEnd.get(i).toDate())
						.build());
				} else {
					results.add(lithium.service.accounting.objects.SummaryAccount.builder()
							.tranCount(0L)
							.debitCents(0L)
							.creditCents(0L)
							.openingBalanceCents(0L)
							.closingBalanceCents(0L)
							.dateStart(dateStart.get(i).toDate())
							.dateEnd(dateEnd.get(i).toDate())
						.build());
				}
			}
		}
		return Response.<List<lithium.service.accounting.objects.SummaryAccount>>builder().data(results).build();
	}
	
	@RequestMapping("/{domain}/findLimitedByOwnerGuid")
	Response<List<lithium.service.accounting.objects.SummaryAccount>> findLimitedByOwnerGuid(
			@PathVariable("domain") String domain,
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode,  
			@RequestParam("currency") String currency, 
			@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateStart,
			@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateEnd) {
		
		Specification<SummaryAccount> spec = Specification.where(
				SummaryAccountSpecifications.find(
						domain, 
						currency, 
						accountCode, 
						granularity,
						dateStart, 
						dateEnd,
						ownerGuid));
		
		List<lithium.service.accounting.objects.SummaryAccount> results = new ArrayList<>();
		for (SummaryAccount tran: repo.findAll(spec, Sort.by(Direction.ASC, "period.dateStart"))) {
			results.add(lithium.service.accounting.objects.SummaryAccount.builder()
					.tranCount(tran.getTranCount())
					.debitCents(tran.getDebitCents())
					.creditCents(tran.getCreditCents())
					.openingBalanceCents(tran.getOpeningBalanceCents())
					.closingBalanceCents(tran.getClosingBalanceCents())
					.dateStart(tran.getPeriod().getDateStart())
					.dateEnd(tran.getPeriod().getDateEnd())
				.build());
		};
		
		return Response.<List<lithium.service.accounting.objects.SummaryAccount>>builder().data(results).build();
	}

	/*
		NOTE: Here was removed legacy code: *aux label summary reverse* feature.
		You can find removed source using git blame on this line
	 */

	@RequestMapping("/get-user-turnover")
	public Long getUserTurnoverFrom(
			@RequestParam("guid") String guid,
			@RequestParam("dateFrom") String dateFrom,
			@RequestParam("accountCodes") List<String> accountCodes,
			@RequestParam("granularity") String granularity
	) {
		return repo.getTurnoverFrom(guid, dateFrom, accountCodes, granularity);
	}
}
