package lithium.service.accounting.provider.internal.controllers;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomainTransactionType;
import lithium.service.accounting.provider.internal.data.repositories.SummaryDomainTransactionTypeRepository;
import lithium.service.accounting.provider.internal.data.repositories.specifications.SummaryDomainTransactionTypeSpecifications;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/summary/domaintrantype/{domain}")
public class SummaryDomainTransactionTypeController {
	
	@Autowired SummaryDomainTransactionTypeRepository repo;
	
	@RequestMapping("/find")
	Response<List<SummaryDomainTransactionType>> find(@PathVariable("domain") String domain, @RequestParam Integer granularity,
			@RequestParam String accountCode, @RequestParam String transactionType, @RequestParam String currency) {
		String[] accountCodes = accountCode.split(",");

		return Response.<List<SummaryDomainTransactionType>>builder()
			.data(repo.findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeInAndTransactionTypeCodeAndCurrencyCodeOrderByPeriodDateStart(
				granularity, domain, accountCodes, transactionType, currency))
			.build();
	}

	@RequestMapping("/findLast")
	Response<List<SummaryTransactionType>> findLast3(
			@PathVariable("domain") String domain, 
			@RequestParam("last") Integer last,
			@RequestParam("granularity") Integer granularity,
			@RequestParam("accountCode") String accountCode, 
			@RequestParam("transactionType") String transactionType, 
			@RequestParam("currency") String currency) {
		
		ArrayList<SummaryTransactionType> results = new ArrayList<>();
		
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
			Specification<SummaryDomainTransactionType> spec = Specification.where(
					SummaryDomainTransactionTypeSpecifications.find(
							domain, 
							currency, 
							accountCode, 
							transactionType, 
							granularity,
							dateStart.get(i).toDate(), 
							dateEnd.get(i).toDate()));
			
			List<SummaryDomainTransactionType> trans = repo.findAll(spec);
			if (!trans.isEmpty()) {
				SummaryTransactionType stt = SummaryTransactionType.builder().build();
				for (SummaryDomainTransactionType tran: trans) {
					stt.setTranCount((stt.getTranCount() != null)? (stt.getTranCount() + tran.getTranCount()): tran.getTranCount());
					stt.setDebitCents((stt.getDebitCents() != null)? (stt.getDebitCents() + tran.getDebitCents()): tran.getDebitCents());
					stt.setCreditCents((stt.getCreditCents() != null)? (stt.getCreditCents() + tran.getCreditCents()): tran.getCreditCents());
					stt.setDateStart(tran.getPeriod().getDateStart());
					stt.setDateStart(tran.getPeriod().getDateEnd());
				}
				results.add(stt);
			} else {
				results.add(SummaryTransactionType.builder()
						.tranCount(0L)
						.debitCents(0L)
						.creditCents(0L)
						.dateStart(dateStart.get(i).toDate())
						.dateEnd(dateEnd.get(i).toDate())
					.build());
			}
		}
		return Response.<List<SummaryTransactionType>>builder().data(results).build();
	}

	@RequestMapping("/findLimited")
	Response<List<SummaryTransactionType>> findLimited(
			@PathVariable("domain") String domain, 
			@RequestParam("granularity") Integer granularity,
			@RequestParam("accountCode") String accountCode, 
			@RequestParam("transactionType") String transactionType, 
			@RequestParam("currency") String currency, 
			@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateStart,
			@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date dateEnd) {
		
		Specification<SummaryDomainTransactionType> spec = Specification.where(
				SummaryDomainTransactionTypeSpecifications.find(
						domain, 
						currency, 
						accountCode, 
						transactionType, 
						granularity,
						dateStart, 
						dateEnd));
		
		List<SummaryTransactionType> results = new ArrayList<>();
		for (SummaryDomainTransactionType tran: repo.findAll(spec, Sort.by(Direction.ASC, "period.dateStart"))) {
			results.add(SummaryTransactionType.builder()
					.tranCount(tran.getTranCount())
					.debitCents(tran.getDebitCents())
					.creditCents(tran.getCreditCents())
					.dateStart(tran.getPeriod().getDateStart())
					.dateEnd(tran.getPeriod().getDateEnd())
				.build());
		};
		
		return Response.<List<SummaryTransactionType>>builder().data(results).build();
	}

}
