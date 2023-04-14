package lithium.service.accounting.domain.summary.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.domain.summary.storage.entities.AccountCode;
import lithium.service.accounting.domain.summary.storage.entities.Currency;
import lithium.service.accounting.domain.summary.storage.entities.Domain;
import lithium.service.accounting.domain.summary.storage.entities.Period;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomain;
import lithium.service.accounting.domain.summary.storage.repositories.SummaryDomainRepository;
import lithium.service.accounting.domain.summary.storage.specifications.SummaryDomainAccountCodeSpecifications;
import lithium.service.accounting.domain.summary.util.GranularityUtil;
import lithium.service.accounting.objects.Account;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.SummaryDomainType;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.client.objects.Granularity;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SummaryDomainService {
	@Autowired private AccountCodeService accountCodeService;
	@Autowired private CurrencyService currencyService;
	@Autowired private DomainService domainService;
	@Autowired private SummaryDomainRepository repository;
	@Autowired private PeriodService periodService;

	@TimeThisMethod
	public List<SummaryDomain> find(String domainName, int granularity, String accountCode, String currency) {
		log.trace("Find | domainName: {}, granularity: {}, accountCode: {}, currency: {}", domainName, granularity,
				accountCode, currency);
		List<SummaryDomain> results = new ArrayList<>();

		SW.start("repository.find");
		List<SummaryDomain> data = repository.
				findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeOrderByPeriodDateStart(
						granularity, domainName, accountCode, currency);
		SW.stop();

		SW.start("groupAndCompress");
		group(data).forEach((period, summaries) -> {
			SummaryDomain summary = compress(summaries);
			results.add(summary);
		});
		SW.stop();

		return results;
	}

	@TimeThisMethod
	public List<SummaryDomainType> findLast(String domainName, int last, int granularity, String accountCode,
			String currency) {
		log.trace("Find last | domainName: {}, last: {}, granularity: {}, accountCode: {}, currency: {}", domainName,
				last, granularity, accountCode, currency);
		List<SummaryDomainType> results = new ArrayList<>();

		Granularity g = Granularity.fromGranularity(granularity);
		if (g.compareTo(Granularity.GRANULARITY_TOTAL) == 0) {
			last = 0;
		}
		Map<String, List<DateTime>> dates = GranularityUtil.getDatesForLastX(g, true, last);

		for (int i = 0; i < last + 1; i++) {
			Date dateStart = dates.get(GranularityUtil.DATES_START).get(i).toDate();
			Date dateEnd = dates.get(GranularityUtil.DATES_END).get(i).toDate();

			Specification<SummaryDomain> spec = Specification.where(
					SummaryDomainAccountCodeSpecifications.find(domainName, currency, accountCode, granularity,
							dateStart, dateEnd));

			SW.start("repository.find_" + i);
			List<SummaryDomain> data = repository.findAll(spec);
			SW.stop();
			if (!data.isEmpty()) {
				SW.start("groupAndCompress");
				group(data).forEach((period, summaries) -> {
					SummaryDomain summary = compress(summaries);
					results.add(SummaryDomainType.builder()
							.tranCount(summary.getTranCount())
							.debitCents(summary.getDebitCents())
							.creditCents(summary.getCreditCents())
							.openingBalanceCents(summary.getOpeningBalanceCents())
							.closingBalanceCents(summary.getClosingBalanceCents())
							.dateStart(summary.getPeriod().getDateStart())
							.dateEnd(summary.getPeriod().getDateEnd())
							.build());
				});
				SW.stop();
			} else {
				List<SummaryDomain> summaries = findSummariesFirstBefore(granularity, domainName,
						accountCode, currency, dateStart);
				if (!summaries.isEmpty()) {
					SummaryDomain summary = compress(summaries);
					results.add(SummaryDomainType.builder()
							.tranCount(0L)
							.debitCents((summary.getClosingBalanceCents() < 0)
									? summary.getClosingBalanceCents()
									: 0L)
							.creditCents((summary.getClosingBalanceCents() > 0)
									? summary.getClosingBalanceCents()
									: 0L)
							.openingBalanceCents(summary.getClosingBalanceCents())
							.closingBalanceCents(summary.getClosingBalanceCents())
							.dateStart(dateStart)
							.dateEnd(dateEnd)
							.build());
				} else {
					results.add(SummaryDomainType.builder()
							.tranCount(0L)
							.debitCents(0L)
							.creditCents(0L)
							.openingBalanceCents(0L)
							.closingBalanceCents(0L)
							.dateStart(dateStart)
							.dateEnd(dateEnd)
							.build());
				}
			}
		}

		if (g.compareTo(Granularity.GRANULARITY_TOTAL) == 0) {
			results.add(1, results.get(0));
			results.add(2, results.get(0));
		}

		return results;
	}

	@TimeThisMethod
	public List<SummaryDomainType> findLimited(String domainName, int granularity, String accountCode, String currency,
			Date dateStart, Date dateEnd) {
		log.trace("Find limited | domainName: {}, granularity: {}, accountCode: {}, currency: {}, dateStart: {}"
				+ ", dateEnd: {}", domainName, granularity, accountCode, currency, dateStart, dateEnd);
		List<SummaryDomainType> results = new ArrayList<>();

		Specification<SummaryDomain> spec = Specification.where(
				SummaryDomainAccountCodeSpecifications.find(domainName, currency, accountCode, granularity, dateStart,
						dateEnd));

		SW.start("repository.find");
		List<SummaryDomain> data = repository.findAll(spec, Sort.by(Sort.Direction.ASC, "period.dateStart"));
		SW.stop();

		SW.start("groupAndCompress");
		group(data).forEach((period, summaries) -> {
			SummaryDomain summary = compress(summaries);
			results.add(SummaryDomainType.builder()
					.tranCount(summary.getTranCount())
					.debitCents(summary.getDebitCents())
					.creditCents(summary.getCreditCents())
					.openingBalanceCents(summary.getOpeningBalanceCents())
					.closingBalanceCents(summary.getClosingBalanceCents())
					.dateStart(summary.getPeriod().getDateStart())
					.dateEnd(summary.getPeriod().getDateEnd())
					.build());
		});
		SW.stop();

		return results;
	}

	public SummaryDomain findByPeriodAndAccountCodeAndCurrency(Period period, AccountCode accountCode, Currency currency) {
		List<SummaryDomain> summaries = repository.findByPeriodAndAccountCodeAndCurrency(period, accountCode, currency);
		if (summaries.isEmpty()) return null;
		SummaryDomain summary = compress(summaries);
		return summary;
	}

	@TimeThisMethod
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void process(String shard, Domain domain, List<CompleteTransaction> transactions) {
		LocalDate now = LocalDate.now();

		for (CompleteTransaction transaction: transactions) {
			SW.start("sd_transaction_" + transaction.getTransactionId());
			for (TransactionEntry entry: transaction.getTransactionEntryList()) {
				LocalDate entryDate = entry.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

				long debitCents = (entry.getAmountCents() > 0) ? entry.getAmountCents() : 0;
				long creditCents = (entry.getAmountCents() > 0) ? 0 : entry.getAmountCents() * -1;
				Account account = entry.getAccount();
				AccountCode accountCode = accountCodeService.findOrCreate(account.getAccountCode().getCode());
				Currency currency = currencyService.findOrCreate(account.getCurrency().getCode(),
						account.getCurrency().getName());

				log.trace("Processing SummaryDomain for transaction entry: {}, debitCents: {}, creditCents: {}"
						+ ", account: {}, accountCode: {}, currency: {}", entry, debitCents, creditCents, account,
						accountCode, currency);

				Arrays.stream(Granularity.values()).forEach(granularity -> {
					if (granularity.granularity().intValue() > Granularity.GRANULARITY_TOTAL.granularity().intValue()) {
						return;
					}

					Period period = periodService.findOrCreatePeriod(new DateTime(entry.getDate()), domain,
							granularity.granularity());

					SummaryDomain summary = repository.findByShardAndAccountCodeAndCurrencyAndPeriod(shard, accountCode,
							currency, period);

					if (summary == null) {
						summary = create(shard, accountCode, currency, period);
					}

					summary.setDebitCents(summary.getDebitCents() + debitCents);
					summary.setCreditCents(summary.getCreditCents() + creditCents);
					summary.setClosingBalanceCents(summary.getClosingBalanceCents() + debitCents - creditCents);
					summary.setTranCount(summary.getTranCount() + 1);
					summary = repository.save(summary);
					log.trace("Updated SummaryDomain: {}", summary);

					if (entryDate.isBefore(now) && granularity != Granularity.GRANULARITY_TOTAL) {
						log.warn("Transaction entry date is in the past. Updating future granularity periods |"
								+ " tranEntryId: {}, entryDate: {}, now: {}, granularity: {}, accountCode: {},"
								+ " currency: {}, amountCents: {}", entry.getId(), entryDate, now, granularity.type(),
								accountCode.getCode(), currency.getCode(), entry.getAmountCents());
						updateFutureGranularityPeriods(entry.getAmountCents(), accountCode, currency, domain,
								granularity, entry.getDate());
					}
				});
			}
			SW.stop();
		}
	}

	private void updateFutureGranularityPeriods(long amountCents, AccountCode accountCode, Currency currency,
	        Domain domain, Granularity granularity, Date date) {
		boolean proceed = true;
		int page = 0;
		Date lastDateProcessed = null;
		while (proceed) {
			Pageable pageRequest = PageRequest.of(page, 50);
			// Locate all granularity periods with start date after adjustment transaction date.
			// Retrieves all shards. Ordered by start date.
			Page<SummaryDomain> pageData = repository.findByAccountCodeAndCurrencyAndPeriodDomainAndPeriodGranularityAndPeriodDateStartAfterOrderByPeriodDateStartAsc(
					accountCode, currency, domain, granularity.granularity(), date, pageRequest);
			for (SummaryDomain summary: pageData.getContent()) {
				// All shards, so if we already processed this date, just skip...
				if (lastDateProcessed != null && lastDateProcessed.equals(summary.getPeriod().getDateStart())) {
					continue;
				}

				// Obtain a lock, then update. No choice but to hold this lock until the end of the entire transaction.
				SummaryDomain update = repository.findByIdForUpdate(summary.getId());
				update.setOpeningBalanceCents(update.getOpeningBalanceCents() + amountCents);
				update.setClosingBalanceCents(update.getClosingBalanceCents() + amountCents);
				update = repository.save(update);

				// Remember the date we just processed, so we don't duplicate the update on another shard!
				lastDateProcessed = summary.getPeriod().getDateStart();
			}
			if (!pageData.hasNext()) proceed = false;
			page++;
		}
	}

	public SummaryDomain save(SummaryDomain summary) {
		return repository.save(summary);
	}

	private SummaryDomain create(String shard, AccountCode accountCode, Currency currency, Period period) {
		// For all granularities except total, bring the balance down from the closest previous period, if not
		// done already on another shard.
		// We ensure this by bringing down the balance on the very first shard being created for the account code,
		// currency, and period combination.
		Long balanceBroughtDown = 0L;
		if (period.getGranularity() != Period.GRANULARITY_TOTAL) {
			// Check if there are any existing period shards. We have to lock the period ensure that no other process
			// creates a shard while we are busy.
			periodService.findForUpdate(period.getId());
			List<SummaryDomain> periodShards = repository.findByPeriodAndAccountCodeAndCurrency(period, accountCode,
					currency);
			if (periodShards.isEmpty()) {
				// No shards yet. Find the closingBalanceCents of the closest previous period.
				List<SummaryDomain> previousPeriodShards = findSummariesFirstBefore(period.getGranularity(),
						period.getDomain().getName(), accountCode.getCode(), currency.getCode(),
						period.getDateStart());
				if (!previousPeriodShards.isEmpty()) {
					// Compress all shards to a single object with values accumulated.
					SummaryDomain previousPeriodSummary = compress(previousPeriodShards);
					// Set the balance to add to opening/closing balance cents of the first period shard about to be
					// created.
					balanceBroughtDown = previousPeriodSummary.getClosingBalanceCents();
				}
			}
		}

		SummaryDomain summary = SummaryDomain.builder()
				.shard(shard)
				.accountCode(accountCode)
				.currency(currency)
				.period(period)
				.openingBalanceCents(balanceBroughtDown)
				.closingBalanceCents(balanceBroughtDown)
				.build();
		summary = repository.save(summary);
		log.trace("Created SummaryDomain: {}", summary);
		return summary;
	}

	private List<SummaryDomain> findSummariesFirstBefore(int granularity, String domainName, String accountCode,
	        String currency, Date dateStart) {
		List<SummaryDomain> results = new ArrayList<>();

		SummaryDomain summary = repository.findFirstByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeAndPeriodDateStartBeforeOrderByPeriodDateStartDesc(
				granularity, domainName, accountCode, currency, dateStart);

		if (summary != null) {
			results = repository.findByPeriodAndAccountCodeAndCurrency(summary.getPeriod(),
					summary.getAccountCode(), summary.getCurrency());
		}

		return results;
	}

	private Map<Period, List<SummaryDomain>> group(List<SummaryDomain> summaries) {
		Map<Period, List<SummaryDomain>> group = new LinkedHashMap<>();
		summaries.stream().forEach(summary -> {
			group.computeIfAbsent(summary.getPeriod(), k -> {
				return new ArrayList<SummaryDomain>();
			}).add(summary);
		});
		return group;
	}

	private SummaryDomain compress(List<SummaryDomain> summaries) {
		SummaryDomain result = SummaryDomain.builder().build();
		summaries.stream().forEach(summary -> {
			if (result.getAccountCode() == null) result.setAccountCode(summary.getAccountCode());
			if (result.getCurrency() == null) result.setCurrency(summary.getCurrency());
			if (result.getPeriod() == null) result.setPeriod(summary.getPeriod());
			result.setTranCount(result.getTranCount() + summary.getTranCount());
			result.setDebitCents(result.getDebitCents() + summary.getDebitCents());
			result.setCreditCents(result.getCreditCents() + summary.getCreditCents());
			result.setOpeningBalanceCents(result.getOpeningBalanceCents() + summary.getOpeningBalanceCents());
			result.setClosingBalanceCents(result.getClosingBalanceCents() + summary.getClosingBalanceCents());
		});
		log.trace("compress | summaries: {}, result: {}", summaries, result);
		return result;
	}
}
