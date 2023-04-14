package lithium.service.accounting.domain.v2.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.domain.v2.storage.entities.Domain;
import lithium.service.accounting.objects.Account;
import lithium.service.accounting.objects.CompleteTransactionV2;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.client.objects.Granularity;
import lithium.service.shards.objects.Shard;
import lithium.shards.ShardsRegistry;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SummaryService {
	@Autowired private SummaryService self;
	@Autowired private AccountCodeService accountCodeService;
	@Autowired private CurrencyService currencyService;
	@Autowired private DomainService domainService;
	@Autowired private LabelValueService labelValueService;
	@Autowired private PeriodService periodService;
	@Autowired private ShardsRegistry shardsRegistry;
	@Autowired private SummaryDomainService summaryDomainService;
	@Autowired private SummaryDomainLabelValueService summaryDomainLabelValueService;
	@Autowired private SummaryDomainTransactionTypeService summaryDomainTransactionTypeService;
	@Autowired private TransactionTypeService transactionTypeService;

	private static final String SHARD_POOL = "AdjustmentQueueProcessor";

	// Persist shared data before the start of the transaction
	@TimeThisMethod
	public void process(String threadName, List<CompleteTransactionV2> transactions)
			throws Status500InternalServerErrorException {
		Shard shard = shardsRegistry.get(SHARD_POOL, threadName);
		SW.start("domain");
		Domain domain = domainService.findOrCreate(transactions.get(0)
				.getTransactionEntryList().get(0)
				.getAccount()
				.getDomain()
				.getName());
		SW.stop();

		SW.start("shared-data.find-or-create");
		for (CompleteTransactionV2 transaction: transactions) {
			transactionTypeService.findOrCreate(transaction.getTransactionType());

			for (TransactionLabelBasic tranLabelBasic: transaction.getTransactionLabelList()) {
				if (tranLabelBasic.isSummarize()) {
					labelValueService.findOrCreate(tranLabelBasic.getLabelName(), tranLabelBasic.getLabelValue());
				}
			}

			for (TransactionEntry entry: transaction.getTransactionEntryList()) {
				Account account = entry.getAccount();
				accountCodeService.findOrCreate(account.getAccountCode().getCode());
				currencyService.findOrCreate(account.getCurrency().getCode(), account.getCurrency().getName());

				Arrays.stream(Granularity.values()).forEach(granularity -> {
					if (granularity.granularity().intValue() <=
							Granularity.GRANULARITY_TOTAL.granularity().intValue()) {
						periodService.findOrCreatePeriod(new DateTime(entry.getDate()), domain,
								granularity.granularity());
					}
				});
			}
		}
		SW.stop();

		self.process(shard, threadName, domain, transactions);
	}

	@TimeThisMethod
	@Transactional(rollbackFor = Exception.class)
	public void process(Shard shard, String threadName, Domain domain, List<CompleteTransactionV2> transactions)
			throws Status500InternalServerErrorException {
		log.trace("{} - Processing domain summaries for transactions | {}", threadName, transactions);

		try {
			SW.start("summaryDomainService.process");
			summaryDomainService.process(shard.getUuid(), domain, transactions);
			SW.stop();

			SW.start("summaryDomainLabelValueService.process");
			summaryDomainLabelValueService.process(shard.getUuid(), domain, transactions);

			SW.stop();

			SW.start("summaryDomainTransactionTypeService.process");
			summaryDomainTransactionTypeService.process(shard.getUuid(), domain, transactions);
			SW.stop();
		} catch (Exception e) {
			log.error("Failed to process adjustment | Transactions: {} | {}", transactions, e.getMessage(), e);
			throw new Status500InternalServerErrorException(e.getMessage());
		}
	}
}
