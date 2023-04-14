package lithium.service.accounting.provider.internal.services;

import lithium.metrics.LithiumMetricsService;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.enums.Granularity;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountLabelValue;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionTypeLabel;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountLabelValueRepository;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SummaryAccountLabelValueService {
	@Autowired CachingDomainClientService cachingDomainClientService;
	@Autowired PeriodService periodService;
	@Autowired SummaryAccountLabelValueRepository repository;
	@Autowired LithiumMetricsService metrics;
	@Autowired TransactionTypeService transactionTypeService;

	/**
	 * This method should be called from lithium.service.accounting.provider.internal.services.TransactionService#summarizeAdditionalTransactionLabels(lithium.service.accounting.objects.TransactionLabelContainer)
	 * The method in TransactionService does the findOrCreate for the Periods and the SummaryAccountLabelValues
	 *
	 * @param transaction
	 * @param entries
	 * @param summaryLabelList
	 * @throws Exception
	 * @see TransactionService#summarizeAdditionalTransactionLabels(lithium.service.accounting.objects.TransactionLabelContainer)
	 */
	@TimeThisMethod
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	public void summarizeAdditionalTransactionLabelsTransactional(Transaction transaction, List<TransactionEntry> entries, List<TransactionLabelValue> summaryLabelList) throws Exception {
		log.debug("Transaction entries found for tran: " + transaction + " the entries: " + entries);
		for (TransactionEntry e : entries) {
			for (TransactionLabelValue tlv: summaryLabelList) {
				SW.start("transactionentry lvadjust " + e.getAccount().getAccountCode().getCode() + " " + e.getAmountCents() + " " + tlv.getLabelValue().getLabel().getName() + "=" + tlv.getLabelValue().getValue());
				log.debug("Going to do summary tran for entry: " + e.toString() + " and transaction label value: " + tlv.toString());
				adjust(e, transaction.getTransactionType(), tlv.getLabelValue());
				log.debug("completed the summary for " + e.toString() + " and transaction label value: " + tlv.toString());
				SW.stop();
			}
		}
	}

	/**
	 * This method should be called from lithium.service.accounting.provider.internal.services.TransactionService#summarizeAdditionalTransactionLabels(long, java.util.List)
	 * The method in TransactionService does the findOrCreate for the Periods and the SummaryAccountLabelValues
	 *
	 * @param transaction
	 * @param entries
	 * @param summaryLabelValues
	 * @throws Exception
	 * @see TransactionService#summarizeAdditionalTransactionLabels(long, java.util.List)
	 */
	@TimeThisMethod
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	public void summarizeAdditionalAuxTransactionLabelsTransactional(Transaction transaction, List<TransactionEntry> entries, List<LabelValue> summaryLabelValues) throws Exception {
		log.debug("Transaction entries found for tran: " + transaction + " the entries: " + entries);
		for (TransactionEntry e : entries) {
			for (LabelValue lv: summaryLabelValues) {
				SW.start("transactionentry lvadjust " + e.getAccount().getAccountCode().getCode() + " " + e.getAmountCents() + " " + lv.getLabel().getName() + "=" + lv.getValue());
				log.debug("Going to do summary tran for entry: " + e + " and transaction label value: " + lv);
				adjust(e, transaction.getTransactionType(), lv);
				log.debug("completed the summary for " + e + " and transaction label value: " + lv);
				SW.stop();
			}
		}
	}

	/*
		NOTE: Here was removed legacy code: *aux label summary reverse* feature.
		You can find removed source using git blame on this line
	 */

	/**
	 * This method should not be called without a transaction, and should only be called from one of the summarize methods above.
	 * We cannot annotate this method with @Transactional(propagation = Propagation.MANDATORY) because it is private, and should remain private.
	 *
	 * @param transactionEntry
	 * @param transactionType
	 * @param labelValue
	 */
	@TimeThisMethod
	private void adjust(TransactionEntry transactionEntry, TransactionType transactionType, LabelValue labelValue) {
		List<String> ignoredLabels = ignoredLabels(transactionEntry.getAccount().getDomain().getName());
		if (!ignoredLabels.isEmpty() && ignoredLabels.contains(labelValue.getLabel().getName())) {
			return;
		}

		Long debitCents = (transactionEntry.getAmountCents() > 0) ? transactionEntry.getAmountCents(): 0;
		Long creditCents = (transactionEntry.getAmountCents() > 0) ? 0: transactionEntry.getAmountCents() * -1;

		TransactionTypeLabel transactionTypeLabel = transactionTypeService.findTransactionTypeLabel(transactionType, labelValue.getLabel().getName());

		if (transactionTypeLabel != null &&
				transactionTypeLabel.getSummarizeTotal() != null &&
				transactionTypeLabel.getSummarizeTotal()
		) {
			SW.start("findOrCreatePeriod " + Granularity.GRANULARITY_TOTAL.id());
			Period period = periodService.findOrCreatePeriod(new DateTime(transactionEntry.getDate().getTime()),
					transactionEntry.getAccount().getDomain(), Granularity.GRANULARITY_TOTAL.id());
			SW.stop();

			adjust(period, transactionEntry.getAccount(), transactionType, labelValue, debitCents, creditCents, true);
		} else {
			for (int granularity = 1; granularity <= 5; granularity++) {
				SW.start("findOrCreatePeriod " + granularity);
				Period period = periodService.findOrCreatePeriod(new DateTime(transactionEntry.getDate().getTime()),
						transactionEntry.getAccount().getDomain(), granularity);
				SW.stop();

				adjust(period, transactionEntry.getAccount(), transactionType, labelValue, debitCents, creditCents, true);
			}
		}
	}

	/**
	 * This method should not be called without a transaction, and should only be called from the adjust method above.
	 * We cannot annotate this method with @Transactional(propagation = Propagation.MANDATORY) because it is private, and should remain private.
	 *
	 * @param period
	 * @param account
	 * @param transactionType
	 * @param labelValue
	 * @param debitCents
	 * @param creditCents
	 * @param logErrorOnCreate
	 */
	@TimeThisMethod
	private void adjust(
		Period period,
		Account account,
		TransactionType transactionType,
		LabelValue labelValue,
		Long debitCents,
		Long creditCents,
		boolean logErrorOnCreate
	) {
		repository.adjust(period, account, transactionType, labelValue, debitCents, creditCents, 1L);
	}

	/**
	 * This is currently being called from lithium.service.accounting.provider.internal.services.TransactionService (one of the summarize methods)
	 * This needs to happen outside of a transaction, with a high retry rate, short delay.
	 *
	 * @param period
	 * @param account
	 * @param transactionType
	 * @param labelValue
	 * @param logErrorOnCreate
	 * @return lithium.service.accounting.provider.internal.data.entities.SummaryAccountLabelValue
	 * @see TransactionService
	 */
	@Retryable(maxAttempts=100, backoff = @Backoff(value = 10, delay = 10))
	public SummaryAccountLabelValue findOrCreate(
			Period period,
			Account account,
			TransactionType transactionType,
			LabelValue labelValue,
			boolean logErrorOnCreate
	) {
		log.debug("find summaryaccountlabelvalue period {} owner {} trantype {} label {} value {}",
				period.getGranularity(), account.getOwner().getGuid(),
				transactionType.getCode(), labelValue.getLabel().getName(), labelValue.getValue());

		SummaryAccountLabelValue existing = repository.findByPeriodAndAccountAndTransactionTypeAndLabelValue(
				period, account, transactionType, labelValue); 
		
		if (existing != null) return existing;
		
		if (logErrorOnCreate) {
			log.info("Creating missing summaryaccountlabelvalue period {} owner {} trantype {} label {} value {}",
					period.getGranularity(), account.getOwner().getGuid(), 
					transactionType.getCode(), labelValue.getLabel().getName(), labelValue.getValue());
		}
		
		SummaryAccountLabelValue summaryAccountLabelValue = SummaryAccountLabelValue.builder()
				.account(account)
				.period(period)
				.transactionType(transactionType)
				.labelValue(labelValue)
				.debitCents(0L).creditCents(0L)
				.damaged(true)
				.tranCount(0L)
				.build();

		return repository.save(summaryAccountLabelValue);
	}

	private List<String> ignoredLabels(String domainName) {
		List<String> ignoredLabels = new ArrayList<>();
		try {
			String ignoredLabelsCommaSepStr = "";
			Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
			Optional<String> setting = domain.findDomainSettingByName(
					DomainSettings.SUMMARY_ACCOUNT_LV_IGNORED_LABELS.key());
			if (setting.isPresent()) {
				ignoredLabelsCommaSepStr = setting.get();
			} else {
				ignoredLabelsCommaSepStr = DomainSettings.SUMMARY_ACCOUNT_LV_IGNORED_LABELS.defaultValue();
			}
			if (!ignoredLabelsCommaSepStr.trim().isEmpty()) {
				ignoredLabels = Arrays.asList(ignoredLabelsCommaSepStr.trim().split(","));
			}
		} catch (Status550ServiceDomainClientException exception) {
			log.error("Failed to retrieve domain from domain service | " + exception.getMessage(), exception);
			// Just continuing with the summarization.
		}
		return ignoredLabels;
	}
}
