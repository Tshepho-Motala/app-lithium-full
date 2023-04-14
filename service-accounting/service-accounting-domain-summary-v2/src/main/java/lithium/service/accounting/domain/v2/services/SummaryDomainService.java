package lithium.service.accounting.domain.v2.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.domain.v2.storage.entities.AccountCode;
import lithium.service.accounting.domain.v2.storage.entities.Currency;
import lithium.service.accounting.domain.v2.storage.entities.Domain;
import lithium.service.accounting.domain.v2.storage.entities.Period;
import lithium.service.accounting.domain.v2.storage.entities.SummaryDomain;
import lithium.service.accounting.domain.v2.storage.repositories.SummaryDomainRepository;
import lithium.service.accounting.domain.v2.storage.specifications.SummaryDomainAccountCodeSpecifications;
import lithium.service.accounting.domain.v2.util.GranularityUtil;
import lithium.service.accounting.domain.v2.util.TestUsersUtil;
import lithium.service.accounting.objects.Account;
import lithium.service.accounting.objects.CompleteTransactionV2;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
	@Autowired private EntityManager entityManager;
	@Autowired private SummaryDomainRepository repository;
	@Autowired private PeriodService periodService;

	@TimeThisMethod
	public List<SummaryDomain> find(String domainName, Boolean testUser, int granularity, String accountCode, String currency) {
		log.trace("Find | domainName: {}, isTestUser: {}, granularity: {}, accountCode: {}, currency: {}", domainName, testUser, granularity,
				accountCode, currency);
		List<SummaryDomain> results = new ArrayList<>();

		SW.start("repository.find");

		List<SummaryDomain> data = null;
		List<Boolean> isTestUserList = TestUsersUtil.createIsTestUserList(testUser);
		data = repository.
					findByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeAndTestUsersInOrderByPeriodDateStart(
							granularity, domainName, accountCode, currency, isTestUserList);
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
	public List<SummaryDomainType> findLast(String domainName, Boolean testUser, int last, int granularity, String accountCode,
											String currency) {
		log.trace("Find last | domainName: {}, testUser {}, last: {}, granularity: {}, accountCode: {}, currency: {}", domainName,
				testUser,last, granularity, accountCode, currency);
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
							dateStart, dateEnd, testUser));

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
				List<SummaryDomain> summaries = findSummariesFirstBefore(granularity,testUser, domainName,
						accountCode, currency, dateStart);
				if (!summaries.isEmpty()) {
					SummaryDomain summary = compress(summaries);
					results.add(SummaryDomainType.builder()
							.tranCount(summary.getTranCount())
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
	public List<SummaryDomainType> findLimited(String domainName, Boolean testUsers, int granularity, String accountCode, String currency,
											   Date dateStart, Date dateEnd) {
		log.trace("Find limited | domainName: {}, TestUsers: {}, granularity: {}, accountCode: {}, currency: {}, dateStart: {}"
				+ ", dateEnd: {}", domainName, testUsers, granularity, accountCode, currency, dateStart, dateEnd);
		List<SummaryDomainType> results = new ArrayList<>();

		Specification<SummaryDomain> spec = Specification.where(
				SummaryDomainAccountCodeSpecifications.find(domainName, currency, accountCode, granularity, dateStart,
						dateEnd, testUsers));

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
	public void process(String shard, Domain domain, List<CompleteTransactionV2> transactions) {
		LocalDate now = LocalDate.now();



		for (CompleteTransactionV2 transaction: transactions) {
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

					SummaryDomain summary = repository.findByShardAndAccountCodeAndCurrencyAndPeriodAndTestUsers(shard,
							accountCode, currency, period, transaction.isTestUser());

					if (summary == null) {
						List<Long> periodIds = new ArrayList<>();
						Arrays.stream(Granularity.values()).forEach(g -> {
							if (g.granularity().intValue() > Granularity.GRANULARITY_TOTAL.granularity().intValue()) {
								return;
							}
							Period p = periodService.findOrCreatePeriod(new DateTime(entry.getDate()), domain,
									g.granularity());
							periodIds.add(p.getId());
						});
						periodService.findForUpdate(periodIds);

						summary = create(shard, accountCode, currency, period,transaction.isTestUser());
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
								granularity, entry.getDate(), transaction.isTestUser());
					}
				});
			}
			SW.stop();
		}
	}



	private void updateFutureGranularityPeriods(long amountCents, AccountCode accountCode, Currency currency,
	        Domain domain, Granularity granularity, Date date, boolean testUsers) {
		boolean proceed = true;
		int page = 0;
		Date lastDateProcessed = null;
		while (proceed) {
			Pageable pageRequest = PageRequest.of(page, 50);
			// Locate all granularity periods with start date after adjustment transaction date.
			// Retrieves all shards. Ordered by start date.
			Page<SummaryDomain> pageData  = repository.findByAccountCodeAndCurrencyAndPeriodDomainAndPeriodGranularityAndPeriodDateStartAfterAndTestUsersOrderByPeriodDateStartAsc(
						accountCode, currency, domain, granularity.granularity(), date, testUsers, pageRequest);

			for (SummaryDomain summary: pageData.getContent()) {
				// All shards, so if we already processed this date, just skip...
				if (lastDateProcessed != null && lastDateProcessed.equals(summary.getPeriod().getDateStart())) {
					continue;
				}

				// Obtain a lock, then update. No choice but to hold this lock until the end of the entire transaction.
				SummaryDomain update = findByIdForUpdate(summary.getId());
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

	@Transactional
	public SummaryDomain findByIdForUpdate(Long id) {
		entityManager.flush();
		entityManager.clear();
		return repository.findByIdForUpdate(id);
	}

	public SummaryDomain save(SummaryDomain summary) {
		return repository.save(summary);
	}

	private SummaryDomain create(String shard, AccountCode accountCode, Currency currency, Period period, boolean isTestUser) {
		// For all granularities except total, bring the balance down from the closest previous period, if not
		// done already on another shard.
		// We ensure this by bringing down the balance on the very first shard being created for the account code,
		// currency, and period combination.
		Long balanceBroughtDown = 0L;
		if (period.getGranularity() != Period.GRANULARITY_TOTAL) {
			List<SummaryDomain> periodShards = repository.findByAccountCodeAndCurrencyAndPeriodAndTestUsers(accountCode, currency,
					period, isTestUser);
			if (periodShards.isEmpty()) {
				// No shards yet. Find the closingBalanceCents of the closest previous period.
				List<SummaryDomain> previousPeriodShards = findSummariesFirstBefore(period.getGranularity(),
						isTestUser, period.getDomain().getName(), accountCode.getCode(), currency.getCode(),
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
				.testUsers(isTestUser)
				.build();
		summary = repository.save(summary);
		log.trace("Created SummaryDomain: {}", summary);
		return summary;
	}


	private List<SummaryDomain> findSummariesFirstBefore(int granularity, Boolean testUsers, String domainName, String accountCode,
														 String currency, Date dateStart) {
		List<SummaryDomain> results = new ArrayList<>();
		List<Boolean> isTestUserList = TestUsersUtil.createIsTestUserList(testUsers);
		SummaryDomain summary = repository
					.findFirstByPeriodGranularityAndPeriodDomainNameAndAccountCodeCodeAndCurrencyCodeAndPeriodDateStartBeforeAndTestUsersInOrderByPeriodDateStartDesc(
							granularity, domainName, accountCode, currency, dateStart, isTestUserList);

		if (summary != null) {
			results = repository.findByPeriodAndAccountCodeAndCurrencyAndTestUsersIn(summary.getPeriod(),
					summary.getAccountCode(), summary.getCurrency(), isTestUserList);
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
