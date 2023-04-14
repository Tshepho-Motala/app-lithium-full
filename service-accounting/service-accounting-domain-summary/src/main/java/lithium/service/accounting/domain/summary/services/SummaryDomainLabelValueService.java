package lithium.service.accounting.domain.summary.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.domain.summary.storage.entities.AccountCode;
import lithium.service.accounting.domain.summary.storage.entities.Currency;
import lithium.service.accounting.domain.summary.storage.entities.Domain;
import lithium.service.accounting.domain.summary.storage.entities.LabelValue;
import lithium.service.accounting.domain.summary.storage.entities.Period;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomainLabelValue;
import lithium.service.accounting.domain.summary.storage.entities.TransactionType;
import lithium.service.accounting.domain.summary.storage.repositories.SummaryDomainLabelValueRepository;
import lithium.service.accounting.domain.summary.storage.specifications.SummaryDomainLabelValueSpecifications;
import lithium.service.accounting.domain.summary.util.GranularityUtil;
import lithium.service.accounting.objects.Account;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.client.objects.Granularity;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SummaryDomainLabelValueService {
	@Autowired private AccountCodeService accountCodeService;
	@Autowired private CurrencyService currencyService;
	@Autowired private LabelValueService labelValueService;
	@Autowired private PeriodService periodService;
	@Autowired private SummaryDomainLabelValueRepository repository;
	@Autowired private TransactionTypeService transactionTypeService;

	@TimeThisMethod
	public List<SummaryDomainLabelValue> find(String domainName, int granularity, String accountCode,
	        String transactionType, String currency, String labelName, String labelValue) {
		log.trace("Find | domainName: {}, granularity: {}, accountCode: {}, transactionType: {}, currency: {}"
				+ ", labelName: {}, labelValue: {}", domainName, granularity, accountCode, transactionType, currency,
				labelName, labelValue);
		List<SummaryDomainLabelValue> results = new ArrayList<>();

		Specification<SummaryDomainLabelValue> spec = Specification.where(SummaryDomainLabelValueSpecifications.find(
				domainName, currency, accountCode, transactionType, labelName, labelValue, granularity));
		SW.start("repository.find");
		List<SummaryDomainLabelValue> data = repository.findAll(spec);
		SW.stop();

		SW.start("groupAndCompress");
		group(data).forEach((period, summaries) -> results.add(compress(summaries)));
		SW.stop();

		return results;
	}

	@TimeThisMethod
	public List<SummaryTransactionType> findLast(String domainName, int last, int granularity, String accountCode,
	        String transactionType, String labelName, String labelValue, String currency) {
		log.trace("Find last | domainName: {}, last: {}, granularity: {}, accountCode: {}, transactionType: {}"
				+ ", labelName: {}, labelValue: {}, currency: {}", domainName, last, granularity, accountCode,
				transactionType, labelName, labelValue, currency);
		List<SummaryTransactionType> results = new ArrayList<>();

		Granularity g = Granularity.fromGranularity(granularity);
		Map<String, List<DateTime>> dates = GranularityUtil.getDatesForLastX(g, false, last);

		for (int i = 0; i < last + 1; i++) {
			Date dateStart = dates.get(GranularityUtil.DATES_START).get(i).toDate();
			Date dateEnd = dates.get(GranularityUtil.DATES_END).get(i).toDate();

			Specification<SummaryDomainLabelValue> spec = Specification.where(
					SummaryDomainLabelValueSpecifications.find(domainName, currency, accountCode, transactionType,
							labelName, labelValue, granularity, dateStart, dateEnd));

			SW.start("repository.find_" + i);
			List<SummaryDomainLabelValue> data = repository.findAll(spec);
			SW.stop();

			if (!data.isEmpty()) {
				SW.start("compress_" + i);
				SummaryDomainLabelValue summary = compress(data);
				results.add(SummaryTransactionType.builder()
						.tranCount(summary.getTranCount())
						.debitCents(summary.getDebitCents())
						.creditCents(summary.getCreditCents())
						.dateStart(summary.getPeriod().getDateStart())
						.dateEnd(summary.getPeriod().getDateEnd())
						.build());
				SW.stop();
			} else {
				results.add(SummaryTransactionType.builder()
						.tranCount(0L)
						.debitCents(0L)
						.creditCents(0L)
						.dateStart(dateStart)
						.dateEnd(dateEnd)
						.build());
			}
		}

		return results;
	}

	@TimeThisMethod
	public List<SummaryLabelValue> findLimited(String domainName, int granularity, String accountCode,
			String transactionType, String labelName, String labelValue, String currency, String dateStart,
			String dateEnd) {
		log.trace("Find limited | domainName: {}, granularity: {}, accountCode: {}, transactionType: {}, labelName: {}"
				+ ", labelValue: {}, currency: {}, dateStart: {}, dateEnd: {}", domainName, granularity, accountCode,
				transactionType, labelName, labelValue, currency, dateStart, dateEnd);
		List<SummaryLabelValue> results = new ArrayList<>();

		Specification<SummaryDomainLabelValue> spec = Specification.where(
				SummaryDomainLabelValueSpecifications.find(
						domainName,
						currency,
						accountCode,
						transactionType,
						labelName,
						labelValue,
						granularity,
						new DateTime(dateStart).toDate(),
						new DateTime(dateEnd).toDate()));

		SW.start("repository.find");
		List<SummaryDomainLabelValue> data = repository.findAll(spec,
				Sort.by(Sort.Direction.ASC, "period.dateStart"));
		SW.stop();

		SW.start("groupAndCompress");
		group(data).forEach((period, summaries) -> {
			SummaryDomainLabelValue summary = compress(summaries);
			results.add(SummaryLabelValue.builder()
					.tranCount(summary.getTranCount())
					.debitCents(summary.getDebitCents())
					.creditCents(summary.getCreditCents())
					.dateStart(summary.getPeriod().getDateStart())
					.dateEnd(summary.getPeriod().getDateEnd())
					.build());
		});
		SW.stop();

		return results;
	}

	public SummaryDomainLabelValue findByAccountCodeAndTransactionTypeAndCurrencyAndPeriodAndLabelValue(
			AccountCode accountCode, TransactionType transactionType, Currency currency, Period period,
			LabelValue labelValue) {
		List<SummaryDomainLabelValue> summaries = repository
				.findByAccountCodeAndTransactionTypeAndCurrencyAndPeriodAndLabelValue(accountCode, transactionType,
						currency, period, labelValue);
		if (summaries.isEmpty()) return null;
		SummaryDomainLabelValue summary = compress(summaries);
		return summary;
	}

	@TimeThisMethod
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void process(String shard, Domain domain, List<CompleteTransaction> transactions) {
		for (CompleteTransaction transaction: transactions) {
			SW.start("sdlv_transaction_" + transaction.getTransactionId() + "_labels");
			List<LabelValue> labelValues = new ArrayList<>(transaction.getTransactionLabelList().size());
			for (TransactionLabelBasic tranLabelBasic: transaction.getTransactionLabelList()) {
				if (tranLabelBasic.isSummarize()) {
					LabelValue labelValue = labelValueService.findOrCreate(tranLabelBasic.getLabelName(),
							tranLabelBasic.getLabelValue());
					labelValues.add(labelValue);
				}
			}
			SW.stop();

			if (!labelValues.isEmpty()) {
				SW.start("sdlv_transaction_" + transaction.getTransactionId());
				TransactionType transactionType = transactionTypeService.findOrCreate(transaction.getTransactionType());
				for (TransactionEntry entry : transaction.getTransactionEntryList()) {
					long debitCents = (entry.getAmountCents() > 0) ? entry.getAmountCents() : 0;
					long creditCents = (entry.getAmountCents() > 0) ? 0 : entry.getAmountCents() * -1;
					Account account = entry.getAccount();
					AccountCode accountCode = accountCodeService.findOrCreate(account.getAccountCode().getCode());
					Currency currency = currencyService.findOrCreate(account.getCurrency().getCode(),
							account.getCurrency().getName());

					log.trace("Processing SummaryDomainLabelValue for transaction entry: {}, debitCents: {}"
									+ ", creditCents: {}, account: {}, accountCode: {}, currency: {}, transactionType: {}"
									+ ", labelValues: {}", entry, debitCents, creditCents, account, accountCode, currency,
							transactionType, labelValues);

					Arrays.stream(Granularity.values()).forEach(granularity -> {
						if (granularity.granularity().intValue() > Granularity.GRANULARITY_TOTAL.granularity().intValue()) {
							return;
						}

						SW.start("sdlv_transaction_" + transaction.getTransactionId() + "_"
								+ granularity.granularity());
						Period period = periodService.findOrCreatePeriod(new DateTime(entry.getDate()), domain,
								granularity.granularity());

						for (LabelValue labelValue : labelValues) {
							SummaryDomainLabelValue summary = repository.findByShardAndAccountCodeAndTransactionTypeAndCurrencyAndPeriodAndLabelValue(
									shard, accountCode, transactionType, currency, period, labelValue);

							if (summary == null) {
								summary = create(shard, accountCode, transactionType, currency, period, labelValue);
							}

							summary.setDebitCents(summary.getDebitCents() + debitCents);
							summary.setCreditCents(summary.getCreditCents() + creditCents);
							summary.setTranCount(summary.getTranCount() + 1);
							summary = repository.save(summary);
							log.trace("Updated SummaryDomainLabelValue: {}", summary);
						}
						SW.stop();
					});
				}
				SW.stop();
			}
		}
	}

	public SummaryDomainLabelValue save(SummaryDomainLabelValue summary) {
		return repository.save(summary);
	}

	private SummaryDomainLabelValue create(String shard, AccountCode accountCode, TransactionType transactionType,
	        Currency currency, Period period, LabelValue labelValue) {
		SummaryDomainLabelValue summary = SummaryDomainLabelValue.builder()
				.shard(shard)
				.accountCode(accountCode)
				.transactionType(transactionType)
				.currency(currency)
				.period(period)
				.labelValue(labelValue)
				.build();
		summary = repository.save(summary);
		log.trace("Created SummaryDomainLabelValue: {}", summary);
		return summary;
	}

	private Map<Period, List<SummaryDomainLabelValue>> group(List<SummaryDomainLabelValue> summaries) {
		Map<Period, List<SummaryDomainLabelValue>> group = new LinkedHashMap<>();
		summaries.stream().forEach(summary -> {
			group.computeIfAbsent(summary.getPeriod(), k -> {
				return new ArrayList<SummaryDomainLabelValue>();
			}).add(summary);
		});
		return group;
	}

	private SummaryDomainLabelValue compress(List<SummaryDomainLabelValue> summaries) {
		SummaryDomainLabelValue result = SummaryDomainLabelValue.builder().build();
		summaries.stream().forEach(summary -> {
			if (result.getLabelValue() == null) result.setLabelValue(summary.getLabelValue());
			if (result.getTransactionType() == null) result.setTransactionType(summary.getTransactionType());
			if (result.getAccountCode() == null) result.setAccountCode(summary.getAccountCode());
			if (result.getCurrency() == null) result.setCurrency(summary.getCurrency());
			if (result.getPeriod() == null) result.setPeriod(summary.getPeriod());
			result.setTranCount(result.getTranCount() + summary.getTranCount());
			result.setDebitCents(result.getDebitCents() + summary.getDebitCents());
			result.setCreditCents(result.getCreditCents() + summary.getCreditCents());
		});
		log.trace("compress | summaries: {}, result: {}", summaries, result);
		return result;
	}
}
