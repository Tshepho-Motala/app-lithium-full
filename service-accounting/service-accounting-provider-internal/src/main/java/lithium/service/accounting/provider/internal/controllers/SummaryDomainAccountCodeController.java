package lithium.service.accounting.provider.internal.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryDomainType;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomain;
import lithium.service.accounting.provider.internal.data.repositories.SummaryDomainRepository;
import lithium.service.accounting.provider.internal.data.repositories.specifications.SummaryDomainAccountCodeSpecifications;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/summary/domainaccountcode/{domain}")
public class SummaryDomainAccountCodeController {
	
	@Autowired SummaryDomainRepository repo;
	
	@RequestMapping("/find")
	Response<List<SummaryDomain>> find(@PathVariable("domain") String domain, @RequestParam Integer granularity,
			@RequestParam String accountCode, @RequestParam String currency) {
		return Response.<List<SummaryDomain>>builder()
				.data(repo.findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeOrderByPeriodDateStart(
						granularity, domain, accountCode, currency))
				.build();
	}

	@RequestMapping("/findLast")
	Response<List<SummaryDomainType>> findLast3(
			@PathVariable("domain") String domain, 
			@RequestParam("last") Integer last,
			@RequestParam("granularity") Integer granularity,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("currency") String currency) {
		
		if (granularity == 5) {
			last = 0;
		}
		ArrayList<SummaryDomainType> results = new ArrayList<>();
		
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
		} else if (granularity == 5) {
			DateTime date = new DateTime(2015, 1, 1, 0, 0);
			dateStart.add(date);
			dateEnd.add(now);
		} else {
			throw new RuntimeException("Invalid granularity: " + granularity);
		}

		for (int i = 0; i < last + 1; i ++) {
			Specification<SummaryDomain> spec = Specification.where(
					SummaryDomainAccountCodeSpecifications.find(
							domain, 
							currency, 
							accountCode,
							granularity,
							dateStart.get(i).toDate(), 
							dateEnd.get(i).toDate()));
			
			SummaryDomain summary = repo.findOne(spec).orElse(null);
			
			if (summary == null) {
				//Find the first available one since we could not find one in the specific range
			}
			if (summary != null) {
				results.add(SummaryDomainType.builder()
						.tranCount(summary.getTranCount())
						.debitCents(summary.getDebitCents())
						.creditCents(summary.getCreditCents())
						.openingBalanceCents(summary.getOpeningBalanceCents())
						.closingBalanceCents(summary.getClosingBalanceCents())
						.dateStart(summary.getPeriod().getDateStart())
						.dateEnd(summary.getPeriod().getDateEnd())
					.build());
			} else {
				//Find the first available one since we could not find one in the specific range
				summary = repo.findFirstByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeAndPeriodDateStartBeforeOrderByPeriodDateStartDesc(granularity, domain, accountCode, currency, dateStart.get(i).toDate());
				if (summary != null) {
					//Use closing balance for opening balance as well and use the original date search range filters for the summary (Now it should work as if the balance never changed)
					results.add(SummaryDomainType.builder()
							.tranCount(0L)
							.debitCents(summary.getClosingBalanceCents() < 0 ? summary.getClosingBalanceCents() : 0L )
							.creditCents(summary.getClosingBalanceCents() > 0 ? summary.getClosingBalanceCents() : 0L )
							.openingBalanceCents(summary.getClosingBalanceCents())
							.closingBalanceCents(summary.getClosingBalanceCents())
							.dateStart(dateStart.get(i).toDate())
							.dateEnd(dateEnd.get(i).toDate())
						.build());
				} else {
					results.add(SummaryDomainType.builder()
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
		if (granularity == 5) {
			results.add(1, results.get(0));
			results.add(2, results.get(0));
		}
		return Response.<List<SummaryDomainType>>builder().data(results).build();
	}

	@RequestMapping("/findLimited")
	Response<List<SummaryDomainType>> findLimited(
			@PathVariable("domain") String domain, 
			@RequestParam("granularity") Integer granularity,
			@RequestParam("accountCode") String accountCode, 
			@RequestParam("currency") String currency, 
			@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateStart,
			@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateEnd) {
		
		Specification<SummaryDomain> spec = Specification.where(
				SummaryDomainAccountCodeSpecifications.find(
						domain, 
						currency, 
						accountCode, 
						granularity,
						dateStart, 
						dateEnd));
		
		List<SummaryDomainType> results = new ArrayList<>();
		for (SummaryDomain tran: repo.findAll(spec, Sort.by(Direction.ASC, "period.dateStart"))) {
			results.add(SummaryDomainType.builder()
					.tranCount(tran.getTranCount())
					.debitCents(tran.getDebitCents())
					.creditCents(tran.getCreditCents())
					.openingBalanceCents(tran.getOpeningBalanceCents())
					.closingBalanceCents(tran.getClosingBalanceCents())
					.dateStart(tran.getPeriod().getDateStart())
					.dateEnd(tran.getPeriod().getDateEnd())
				.build());
		};
		
		return Response.<List<SummaryDomainType>>builder().data(results).build();
	}

}
