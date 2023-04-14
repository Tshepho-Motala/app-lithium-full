package lithium.service.accounting.provider.internal.controllers;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomainLabelValue;
import lithium.service.accounting.provider.internal.data.repositories.SummaryDomainLabelValueRepository;
import lithium.service.accounting.provider.internal.data.repositories.specifications.SummaryDomainLabelValueSpecifications;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/summary/domainlabelvalue/{domain}")
public class SummaryDomainLabelValueController {
	@Autowired SummaryDomainLabelValueRepository repo;
	
	@RequestMapping("/find")
	public Response<List<SummaryDomainLabelValue>> find(
		@PathVariable("domain") String domain,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency,
		@RequestParam("labelName") String labelName,
		@RequestParam("labelValue") String labelValue
	) {
		Specification<SummaryDomainLabelValue> spec = Specification.where(
			SummaryDomainLabelValueSpecifications.find(
				domain,
				currency,
				accountCode,
				transactionType,
				labelName,
				labelValue,
				granularity
			)
		);
		return Response.<List<SummaryDomainLabelValue>>builder().data(repo.findAll(spec)).build();
	}

	@RequestMapping("/findLast")
	Response<List<SummaryTransactionType>> findLast3(
		@PathVariable("domain") String domain,
		@RequestParam("last") int last,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("labelName") String labelName,
		@RequestParam("labelValue") String labelValue,
		@RequestParam("currency") String currency
	) {
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
			Specification<SummaryDomainLabelValue> spec = Specification.where(
				SummaryDomainLabelValueSpecifications.find(
					domain,
					currency,
					accountCode,
					transactionType,
					labelName,
					labelValue,
					granularity,
					dateStart.get(i).toDate(),
					dateEnd.get(i).toDate()
				)
			);

			SummaryDomainLabelValue tran = repo.findOne(spec).orElse(null);
			if (tran != null) {
				results.add(
					SummaryTransactionType.builder()
					.tranCount(tran.getTranCount())
					.debitCents(tran.getDebitCents())
					.creditCents(tran.getCreditCents())
					.dateStart(tran.getPeriod().getDateStart())
					.dateEnd(tran.getPeriod().getDateEnd())
					.build()
				);
			} else {
				results.add(
					SummaryTransactionType.builder()
					.tranCount(0L)
					.debitCents(0L)
					.creditCents(0L)
					.dateStart(dateStart.get(i).toDate())
					.dateEnd(dateEnd.get(i).toDate())
					.build()
				);
			}
		}
		return Response.<List<SummaryTransactionType>>builder().data(results).build();
	}

	@RequestMapping("/findLimited")
	Response<List<SummaryLabelValue>> findLimited(
		@PathVariable("domain") String domain,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("labelName") String labelName,
		@RequestParam("labelValue") String labelValue,
		@RequestParam("currency") String currency,
		@RequestParam("dateStart") String dateStart,
		@RequestParam("dateEnd") String dateEnd
	) {
		
		Specification<SummaryDomainLabelValue> spec = Specification.where(
			SummaryDomainLabelValueSpecifications.find(
				domain,
				currency,
				accountCode,
				transactionType,
				labelName,
				labelValue,
				granularity,
				new DateTime(dateStart).toDate(),
				new DateTime(dateEnd).toDate())
		);

		List<SummaryLabelValue> results = new ArrayList<>();
		for (SummaryDomainLabelValue tran: repo.findAll(spec, Sort.by(Direction.ASC, "period.dateStart"))) {
			results.add(
				SummaryLabelValue.builder()
				.tranCount(tran.getTranCount())
				.debitCents(tran.getDebitCents())
				.creditCents(tran.getCreditCents())
				.dateStart(tran.getPeriod().getDateStart())
				.dateEnd(tran.getPeriod().getDateEnd())
				.build()
			);
		};
		
		return Response.<List<SummaryLabelValue>>builder().data(results).build();
	}
}
