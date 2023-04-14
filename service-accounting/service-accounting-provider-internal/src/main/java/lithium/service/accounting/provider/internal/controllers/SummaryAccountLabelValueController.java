package lithium.service.accounting.provider.internal.controllers;

import lithium.service.Response;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.accounting.objects.SummaryLabelValueTotal;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountLabelValue;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountLabelValueType;
import lithium.service.accounting.provider.internal.data.repositories.PeriodRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountLabelValueRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionEntryRepository;
import lithium.service.accounting.provider.internal.data.repositories.specifications.SummaryAccountLabelValueSpecifications;
import lithium.service.accounting.provider.internal.data.repositories.specifications.TransactionEntrySpecifications;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/summary/accountlabelvalue")
public class SummaryAccountLabelValueController {
	@Autowired
	private SummaryAccountLabelValueRepository summaryAccountLabelValueRepository;
	@Autowired
	private TransactionEntryRepository transactionEntryRepository;
	// TODO: refactor to move this logic to a service
	@Autowired
	private PeriodRepository periodRepository;

	@RequestMapping("/find")
	Response<List<SummaryAccountLabelValue>> find(
		@RequestParam("domainName") String domainName,
		@RequestParam("periodId") Long periodId,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("transactionTypeCode") String transactionTypeCode,
		@RequestParam("labelValue") String labelValue,
		@RequestParam("labelName") String labelName,
		@RequestParam("currencyCode") String currencyCode
	) throws Exception {
		Specification<SummaryAccountLabelValue> spec = Specification.where(
				SummaryAccountLabelValueSpecifications.find(
					domainName, periodId, accountCode, transactionTypeCode, labelValue, labelName, currencyCode));
		return Response.<List<SummaryAccountLabelValue>>builder().data(summaryAccountLabelValueRepository.findAll(spec)).build();
	}

	@RequestMapping("/find-multiple-tran-types")
	Response<List<SummaryAccountLabelValue>> find(
			@RequestParam("domainName") String domainName,
			@RequestParam("periodId") Long periodId,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("transactionTypeCodes") List<String> transactionTypeCodes,
			@RequestParam("labelValue") String labelValue,
			@RequestParam("labelName") String labelName,
			@RequestParam("currencyCode") String currencyCode
	) throws Exception {
		Specification<SummaryAccountLabelValue> spec = Specification.where(
				SummaryAccountLabelValueSpecifications.find(
						domainName, periodId, accountCode, transactionTypeCodes, labelValue, labelName, currencyCode));
		return Response.<List<SummaryAccountLabelValue>>builder().data(summaryAccountLabelValueRepository.findAll(spec)).build();
	}


	@RequestMapping("/find/{granularity}/{transactionTypeCode}/{accountCode}/{ownerGuid}/{domainName}/{currencyCode}/{labelValue}/{labelName}")
	Response<SummaryAccountLabelValueType> summaryAccountLabelValueType(
		@PathVariable("granularity") int granularity,
		@PathVariable("transactionTypeCode") String transactionTypeCode,
		@PathVariable("accountCode") String accountCode,
		@PathVariable("ownerGuid") String ownerGuid,
		@PathVariable("domainName") String domainName,
		@PathVariable("currencyCode") String currencyCode,
		@PathVariable("labelValue") String labelValue,
		@PathVariable("labelName") String labelName
	) throws Exception {

		Specification<TransactionEntry> spec = Specification.where(
				TransactionEntrySpecifications.find(
						transactionTypeCode,
						accountCode,
						URLDecoder.decode(ownerGuid, "UTF-8"),
						domainName,
						currencyCode,
						labelValue,
						labelName));

		SummaryAccountLabelValueType data = SummaryAccountLabelValueType.builder()
				.account(null)
				.creditCents(0L)
				.currency(null)
				.debitCents(0L)
				.labelValue(null)
				.period(null)
				.tranCount(0L)
				.transactionType(null)
				.build();

		for (TransactionEntry tran: transactionEntryRepository.findAll(spec)) {
			data.setAccount(tran.getAccount());
			data.setCurrency(tran.getAccount().getCurrency());
			data.setTranCount(data.getTranCount() + 1L);
			if (tran.getAmountCents().longValue() >= 0L) {
				data.setDebitCents(data.getDebitCents() + tran.getAmountCents());
			} else {
				data.setCreditCents(data.getCreditCents() + (tran.getAmountCents() * -1L));
			}
		};

		return Response.<SummaryAccountLabelValueType>builder().data(data).build();
	}

	@RequestMapping("/{domain}/findLimited")
	Response<List<SummaryLabelValue>> findLimited(
			@PathVariable("domain") String domain,
			@RequestParam("granularity") int granularity,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("transactionType") String transactionType,
			@RequestParam("labelName") String labelName,
			@RequestParam("labelValue") String labelValue,
			@RequestParam("currency") String currency,
			@RequestParam("dateStart") String dateStart,
			@RequestParam("dateEnd") String dateEnd,
			@RequestParam("ownerGuid") String ownerGuid) {

		Specification<SummaryAccountLabelValue> spec = Specification.where(
				SummaryAccountLabelValueSpecifications.find(
						domain,
						currency,
						accountCode,
						transactionType,
						granularity,
						new DateTime(dateStart).toDate(),
						new DateTime(dateEnd).toDate(),
						ownerGuid,
						labelName,
						labelValue));

		List<SummaryLabelValue> results = new ArrayList<>();
		for (SummaryAccountLabelValue tran: summaryAccountLabelValueRepository.findAll(spec, Sort.by(Direction.ASC, "period.dateStart"))) {
			results.add(SummaryLabelValue.builder()
					.tranCount(tran.getTranCount())
					.debitCents(tran.getDebitCents())
					.creditCents(tran.getCreditCents())
					.dateStart(tran.getPeriod().getDateStart())
					.dateEnd(tran.getPeriod().getDateEnd())
				.build());
		};

		return Response.<List<SummaryLabelValue>>builder().data(results).build();
	}

	@RequestMapping("/{domain}/find-summary-label-value-total")
	Response<List<SummaryLabelValueTotal>> findSummaryLabelValueTotal(
			@PathVariable("domain") String domain,
			@RequestParam("labelName") String labelName,
			@RequestParam("labelValues") List<String> labelValuesOriginal,
			@RequestParam("userGuid") String userGuid) {

		List<SummaryLabelValueTotal> results = new ArrayList<>();

		// There will only be 1 period for the total granularity per domain
		Period periodGranularityTotal = periodRepository.findTop1ByDomainNameAndGranularity(domain, Period.GRANULARITY_TOTAL);

		for (String labelValue: labelValuesOriginal) {
			List<String> labelValues = new ArrayList<>();
			labelValues.add(labelValue);
			Specification<SummaryAccountLabelValue> spec = Specification.where(
					SummaryAccountLabelValueSpecifications.find(
							periodGranularityTotal,
							labelName,
							labelValues,
							userGuid));

			for (SummaryAccountLabelValue tran: summaryAccountLabelValueRepository.findAll(spec)) {
				results.add(SummaryLabelValueTotal.builder()
						.tranCount(tran.getTranCount())
						.debitCents(tran.getDebitCents())
						.creditCents(tran.getCreditCents())
						.accountCode(tran.getAccount().getAccountCode().getCode())
						.labelValue(tran.getLabelValue().getValue())
						.currencyCode(tran.getAccount().getCurrency().getCode())
					.build());
			};
		}

		return Response.<List<SummaryLabelValueTotal>>builder().data(results).build();
	}
}
