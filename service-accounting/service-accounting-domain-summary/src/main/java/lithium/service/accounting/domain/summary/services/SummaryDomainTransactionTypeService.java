package lithium.service.accounting.domain.summary.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.domain.summary.storage.entities.AccountCode;
import lithium.service.accounting.domain.summary.storage.entities.Currency;
import lithium.service.accounting.domain.summary.storage.entities.Domain;
import lithium.service.accounting.domain.summary.storage.entities.Period;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomainTransactionType;
import lithium.service.accounting.domain.summary.storage.entities.TransactionType;
import lithium.service.accounting.domain.summary.storage.repositories.SummaryDomainTransactionTypeRepository;
import lithium.service.accounting.domain.summary.storage.specifications.SummaryDomainTransactionTypeSpecifications;
import lithium.service.accounting.domain.summary.util.GranularityUtil;
import lithium.service.accounting.objects.Account;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.accounting.objects.TransactionEntry;
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
public class SummaryDomainTransactionTypeService {
	@Autowired private AccountCodeService accountCodeService;
	@Autowired private CurrencyService currencyService;
	@Autowired private PeriodService periodService;
	@Autowired private SummaryDomainTransactionTypeRepository repository;
	@Autowired private TransactionTypeService transactionTypeService;

	@TimeThisMethod
	public List<SummaryDomainTransactionType> find(String domainName, int granularity, String accountCode,
	        String transactionType, String currency) {
		log.trace("Find | domainName: {}, granularity: {}, accountCode: {}, transactionType: {}, currency: {}",
				domainName, granularity, accountCode, transactionType, currency);
		List<SummaryDomainTransactionType> results = new ArrayList<>();

		String[] accountCodes = accountCode.split(",");
		SW.start("repository.find");
		List<SummaryDomainTransactionType> data =
				repository.findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeInAndTransactionTypeCodeAndCurrencyCodeOrderByPeriodDateStart(
					granularity, domainName, accountCodes, transactionType, currency);
		SW.stop();

		SW.start("groupAndCompress");
		group(data).forEach((period, summaries) -> results.add(compress(summaries)));
		SW.stop();

		return results;
	}

	@TimeThisMethod
	public List<SummaryTransactionType> findLast(String domainName, int last, int granularity, String accountCode,
	        String transactionType, String currency) {
		log.trace("Find last | domainName: {}, last: {}, granularity: {}, accountCode: {}, transactionType: {}"
				+ ", currency: {}", domainName, last, granularity, accountCode, transactionType, currency);
		List<SummaryTransactionType> results = new ArrayList<>();

		Granularity g = Granularity.fromGranularity(granularity);
		Map<String, List<DateTime>> dates = GranularityUtil.getDatesForLastX(g, false, last);

		for (int i = 0; i < last + 1; i++) {
			Date dateStart = dates.get(GranularityUtil.DATES_START).get(i).toDate();
			Date dateEnd = dates.get(GranularityUtil.DATES_END).get(i).toDate();

			Specification<SummaryDomainTransactionType> spec = Specification.where(
					SummaryDomainTransactionTypeSpecifications.find(domainName, currency, accountCode, transactionType,
							granularity, dateStart, dateEnd));

			SW.start("repository.find_" + i);
			List<SummaryDomainTransactionType> data = repository.findAll(spec);
			SW.stop();

			if (!data.isEmpty()) {
				SW.start("compress_" + i);
				SummaryDomainTransactionType summary = compress(data);
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
	public List<SummaryTransactionType> findLimited(String domainName, int granularity, String accountCode,
	        String transactionType, String currency, Date dateStart, Date dateEnd) {
		log.trace("Find limited | domainName: {}, granularity: {}, accountCode: {}, transactionType: {}, currency: {}"
				+ ", dateStart: {}, dateEnd: {}", domainName, granularity, accountCode, transactionType, currency,
				dateStart, dateEnd);
		List<SummaryTransactionType> results = new ArrayList<>();

		Specification<SummaryDomainTransactionType> spec = Specification.where(
				SummaryDomainTransactionTypeSpecifications.find(domainName, currency, accountCode, transactionType,
						granularity, dateStart, dateEnd));

		SW.start("repository.find");
		List<SummaryDomainTransactionType> data = repository.findAll(spec,
				Sort.by(Sort.Direction.ASC, "period.dateStart"));
		SW.stop();

		SW.start("groupAndCompress");
		group(data).forEach((period, summaries) -> {
			SummaryDomainTransactionType summary = compress(summaries);
			results.add(SummaryTransactionType.builder()
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
	
	public SummaryDomainTransactionType findByAccountCodeAndTransactionTypeAndCurrencyAndPeriod(AccountCode accountCode,
	        TransactionType transactionType, Currency currency, Period period) {
		List<SummaryDomainTransactionType> summaries = repository
				.findByAccountCodeAndTransactionTypeAndCurrencyAndPeriod(accountCode, transactionType, currency,
						period);
		if (summaries.isEmpty()) return null;
		SummaryDomainTransactionType summary = compress(summaries);
		return summary;
	}

	@TimeThisMethod
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void process(String shard, Domain domain, List<CompleteTransaction> transactions) {
		for (CompleteTransaction transaction: transactions) {
			SW.start("sdtt_transaction_" + transaction.getTransactionId());
			TransactionType transactionType = transactionTypeService.findOrCreate(transaction.getTransactionType());
			for (TransactionEntry entry: transaction.getTransactionEntryList()) {
				long debitCents = (entry.getAmountCents() > 0) ? entry.getAmountCents() : 0;
				long creditCents = (entry.getAmountCents() > 0) ? 0 : entry.getAmountCents() * -1;
				Account account = entry.getAccount();
				AccountCode accountCode = accountCodeService.findOrCreate(account.getAccountCode().getCode());
				Currency currency = currencyService.findOrCreate(account.getCurrency().getCode(),
						account.getCurrency().getName());

				log.trace("Processing SummaryDomainTransactionType for transaction entry: {}, debitCents: {}"
						+ ", creditCents: {}, account: {}, accountCode: {}, currency: {}, transactionType: {}",
						entry, debitCents, creditCents, account, accountCode, currency,
						transactionType);

				Arrays.stream(Granularity.values()).forEach(granularity -> {
					if (granularity.granularity().intValue() > Granularity.GRANULARITY_TOTAL.granularity().intValue()) {
						return;
					}

					Period period = periodService.findOrCreatePeriod(new DateTime(entry.getDate()), domain,
							granularity.granularity());

					SummaryDomainTransactionType summary = repository.findByShardAndAccountCodeAndTransactionTypeAndCurrencyAndPeriod(
							shard, accountCode, transactionType, currency, period);

					if (summary == null) {
						summary = create(shard, accountCode, transactionType, currency, period);
					}

					summary.setDebitCents(summary.getDebitCents() + debitCents);
					summary.setCreditCents(summary.getCreditCents() + creditCents);
					summary.setTranCount(summary.getTranCount() + 1);
					summary = repository.save(summary);
					log.trace("Updated SummaryDomainTransactionType: {}", summary);
				});
			}
			SW.stop();
		}
	}

	public SummaryDomainTransactionType save(SummaryDomainTransactionType summary) {
		return repository.save(summary);
	}

	private SummaryDomainTransactionType create(String shard, AccountCode accountCode, TransactionType transactionType,
			Currency currency, Period period) {
		SummaryDomainTransactionType summary = SummaryDomainTransactionType.builder()
				.shard(shard)
				.accountCode(accountCode)
				.transactionType(transactionType)
				.currency(currency)
				.period(period)
				.build();
		summary = repository.save(summary);
		log.trace("Created SummaryDomainTransactionType: {}", summary);
		return summary;
	}

	private Map<Period, List<SummaryDomainTransactionType>> group(List<SummaryDomainTransactionType> summaries) {
		Map<Period, List<SummaryDomainTransactionType>> group = new LinkedHashMap<>();
		summaries.stream().forEach(summary -> {
			group.computeIfAbsent(summary.getPeriod(), k -> {
				return new ArrayList<SummaryDomainTransactionType>();
			}).add(summary);
		});
		return group;
	}

	private SummaryDomainTransactionType compress(List<SummaryDomainTransactionType> summaries) {
		SummaryDomainTransactionType result = SummaryDomainTransactionType.builder().build();
		summaries.stream().forEach(summary -> {
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
