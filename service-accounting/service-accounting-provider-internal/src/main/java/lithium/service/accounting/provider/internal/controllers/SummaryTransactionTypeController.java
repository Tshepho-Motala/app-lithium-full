package lithium.service.accounting.provider.internal.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountTransactionType;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountTransactionTypeRepository;
import lithium.service.accounting.provider.internal.data.repositories.specifications.SummaryAccountTransactionTypeSpecifications;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/summary/trantype")
public class SummaryTransactionTypeController {
	@Autowired SummaryAccountTransactionTypeRepository repo;

	@RequestMapping("/find")
	Response<SummaryAccountTransactionType> find(
		@RequestParam("accountId") Long accountId,
		@RequestParam("periodId") Long periodId,
		@RequestParam("transactionTypeCode") String transactionTypeCode
	) throws Exception {
		return Response.<SummaryAccountTransactionType>builder().data(
				repo.findByPeriodIdAndAccountIdAndTransactionTypeCode(periodId, accountId, transactionTypeCode))
				.status(Status.OK).build();
	};
	
	@RequestMapping("/find/{accountCode}/{domainName}/{ownerGuid}/{granularity}/{currencyCode}")
	Response<SummaryAccountTransactionType> find(
		@PathVariable("accountCode") String accountCode,
		@PathVariable("domainName") String domainName,
		@PathVariable("ownerGuid") String ownerGuid,
		@PathVariable("granularity") int granularity,
		@PathVariable("currencyCode") String currencyCode
	) throws Exception {
		return Response.<SummaryAccountTransactionType>builder().data(
			repo.findByAccountAccountCodeCodeAndAccountOwnerGuidAndAccountDomainNameAndPeriodGranularityAndAccountCurrencyCode(
				accountCode,
				URLDecoder.decode(ownerGuid, "UTF-8"),
				domainName,
				granularity,
				currencyCode
			)
		).build();
	}
	
	@RequestMapping("/{domain}/findByOwnerGuid")
	Response<List<SummaryAccountTransactionType>> findByUser(
		@PathVariable("domain") String domain,
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("granularity") int granularity,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionType") String transactionType,
		@RequestParam("currency") String currency
	) throws Exception {
		String[] accountCodes = accountCode.split(",");

		return Response.<List<SummaryAccountTransactionType>>builder().data(
			repo.findByAccountOwnerGuidAndPeriodGranularityAndPeriodDomainNameAndAccountAccountCodeCodeInAndTransactionTypeCodeAndAccountCurrencyCodeOrderByPeriodDateStart(
				ownerGuid, granularity, domain, accountCodes, transactionType, currency
			)
		).build();
	}
	
	@RequestMapping("/{domain}/findLastByOwnerGuid")
	Response<List<SummaryTransactionType>> findLastByOwnerGuid(
			@PathVariable("domain") String domain,
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("last") int last,
			@RequestParam("granularity") int granularity,
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
			Specification<SummaryAccountTransactionType> spec = Specification.where(
				SummaryAccountTransactionTypeSpecifications.find(
					domain,
					currency,
					accountCode,
					transactionType,
					granularity,
					dateStart.get(i).toDate(),
					dateEnd.get(i).toDate(),
					ownerGuid
				)
			);
			
			List<SummaryAccountTransactionType> trans = repo.findAll(spec);
			if (!trans.isEmpty()) {
				SummaryTransactionType summaryTransactionType = SummaryTransactionType.builder().build();
				for (SummaryAccountTransactionType satt: trans) {
					summaryTransactionType.setTranCount((summaryTransactionType.getTranCount() != null)? summaryTransactionType.getTranCount() + satt.getTranCount(): satt.getTranCount());
					summaryTransactionType.setDebitCents((summaryTransactionType.getDebitCents() != null)? summaryTransactionType.getDebitCents() + satt.getDebitCents(): satt.getDebitCents());
					summaryTransactionType.setCreditCents((summaryTransactionType.getCreditCents() != null)? summaryTransactionType.getCreditCents() + satt.getCreditCents(): satt.getCreditCents());
					summaryTransactionType.setDateStart(satt.getPeriod().getDateStart());
					summaryTransactionType.setDateEnd(satt.getPeriod().getDateEnd());
				}
				results.add(summaryTransactionType);
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
	
	@RequestMapping("/{domain}/findLimitedByOwnerGuid")
	Response<List<SummaryTransactionType>> findLimitedByOwnerGuid(
			@PathVariable("domain") String domain,
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode, 
			@RequestParam("transactionType") String transactionType, 
			@RequestParam("currency") String currency, 
			@RequestParam("dateStart") String dateStart,
			@RequestParam("dateEnd") String dateEnd
	) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Specification<SummaryAccountTransactionType> spec = Specification.where(
				SummaryAccountTransactionTypeSpecifications.find(
						domain, 
						currency, 
						accountCode, 
						transactionType, 
						granularity,
						sdf.parse(dateStart),
						sdf.parse(dateEnd),
						ownerGuid));
		
		List<SummaryTransactionType> results = new ArrayList<>();
		for (SummaryAccountTransactionType tran: repo.findAll(spec, Sort.by(Direction.ASC, "period.dateStart"))) {
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
    @RequestMapping("/{domain}/findTypesByOwnerGuid")
    Response<List<SummaryAccountTransactionType>> findTypesByOwnerGuid(
            @PathVariable("domain") String domain,
            @RequestParam("ownerGuid") String ownerGuid,
            @RequestParam("granularity") int granularity,
            @RequestParam("accountCode") String accountCode,
            @RequestParam("transactionTypes") List<String> transactionTypes
    ) {
        return Response.<List<SummaryAccountTransactionType>>builder().data(
                repo.findByAccountOwnerGuidAndAccountAccountCodeCodeAndTransactionTypeCodeInAndPeriodGranularity(ownerGuid, accountCode, transactionTypes, granularity)
        ).build();
    }

}
