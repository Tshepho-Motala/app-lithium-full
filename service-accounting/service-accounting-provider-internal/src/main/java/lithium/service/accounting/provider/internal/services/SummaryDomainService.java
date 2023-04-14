package lithium.service.accounting.provider.internal.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import lithium.cashier.CashierTransactionLabels;
import lithium.service.client.util.LabelManager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import lithium.metrics.LithiumMetricsService;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryDomain;
import lithium.service.accounting.provider.internal.data.entities.SummaryProcessingBoundary;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue;
import lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeGroup;
import lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeLabelValueGroup;
import lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeTransactionTypeGroup;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountGroup;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryDomainRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryProcessingBoundaryRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionEntryRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionLabelValueRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SummaryDomainService {

	@Autowired PeriodService periodService;

	@Autowired SummaryDomainRepository repository;
	@Autowired SummaryAccountRepository summaryAccountRepository;
	@Autowired SummaryProcessingBoundaryRepository summaryProcessingBoundaryRepository;
	@Autowired TransactionRepository transactionRepository;
	@Autowired TransactionEntryRepository transactionEntryRepository;
	@Autowired LithiumMetricsService metrics;
	@Autowired TransactionLabelValueRepository transactionLabelValueRepository;

	@Value("${lithium.service.accounting.provider.internal.services.maxTranToProcess:1000}")
	private long maxTranToProcess;

	public SummaryProcessingBoundary getUnsummarizedPeriodSets(LinkedHashSet<PeriodAccountCodeGroup> periodAccountCodeSet,
	                                                           LinkedHashSet<PeriodAccountCodeTransactionTypeGroup> periodAccountCodeTransactionTypeSet,
	                                                           LinkedHashSet<PeriodAccountCodeLabelValueGroup> periodAccountCodeLabelValueSet,
	                                                           boolean historical) throws Exception {

		return metrics.timer(log).time("getPeriodList", (StopWatch sw) -> {
			Date cutoffLimit = DateTime.now().minusMinutes(2).toDate();
			sw.start("findFirstBySummaryType");
			SummaryProcessingBoundary summaryProcessingBoundary = summaryProcessingBoundaryRepository.findFirstBySummaryType(historical ? SummaryProcessingBoundary.DOMAIN_ALL_HISTORICAL : SummaryProcessingBoundary.DOMAIN_ALL);
			sw.stop();
			long boundaryStart = 0L;
			long boundaryEnd = 0L;
			if (summaryProcessingBoundary != null) {
				boundaryStart = summaryProcessingBoundary.getLastTransactionIdProcessed();
				log.trace("Boundary start retrieved from db: " + boundaryStart);
			} else {
				if (historical) {
					log.info("No historical boundary offset found, skipping.");
					return null;
				}
				summaryProcessingBoundary = summaryProcessingBoundaryRepository.save(
						SummaryProcessingBoundary.builder()
								.summaryType(SummaryProcessingBoundary.DOMAIN_ALL)
								.lastTransactionIdProcessed(0L)
								.lastTransactionLabelValueIdProcessed(0L)
								.build());
			}

			boundaryEnd = boundaryStart + maxTranToProcess;
			log.trace("Boundary end calculated from boundary start: " + boundaryStart + " end: " + boundaryEnd);
			long counter = 1;
			do {
				sw.start("allocatePeriodsToExistingTransactions"+boundaryStart+"_"+boundaryEnd);
				boundaryEnd = boundaryStart + (maxTranToProcess * counter);
				log.trace("Boundary end calculated from boundary start and multiplier: " + boundaryStart + " end: " + boundaryEnd + " counter: " + counter);
				//Means we will reach the last tran id in the system, so we can look at labelvalue only trans that we might have missed
				if (transactionRepository.findFirstByIdGreaterThanAndClosedOnLessThan(boundaryEnd, cutoffLimit) == null) {
					if (historical) {
						// We have reached the end of transactions. Delete historical entry
						summaryProcessingBoundaryRepository.delete(summaryProcessingBoundary);
						return null;
					}
					log.trace("Boundary end greater than last tran in db, going to check for label value trans we might have missed using transactionLabelValueId: " + summaryProcessingBoundary.getLastTransactionLabelValueIdProcessed());
					//TransactionLabelValue tlv = transactionLabelValueRepository.findFirstByIdGreaterThanOrderByTransactionId(summaryProcessingBoundary.getLastTransactionLabelValueIdProcessed());
					Long tlvMinTransactionId = transactionLabelValueRepository.findMinTransactionIdGreaterThanId(summaryProcessingBoundary.getLastTransactionLabelValueIdProcessed());
					//Get smallest tranId when comparing lastTranProcessed and last
					if (tlvMinTransactionId != null && tlvMinTransactionId < boundaryStart) {
						log.trace("Boundary start limit will be set by missed transaction label valuestart: " + boundaryStart + " end: " + boundaryEnd + "tlv tran id: "+ tlvMinTransactionId);
						boundaryStart = tlvMinTransactionId;
					}
				}

				boundaryEnd = allocatePeriodsToExistingTransactions(boundaryStart, boundaryEnd,
						summaryProcessingBoundary,
						periodAccountCodeSet,
						periodAccountCodeTransactionTypeSet,
						periodAccountCodeLabelValueSet,
						cutoffLimit);
				counter ++;
				sw.stop();
			} while (boundaryEnd == 0 && transactionRepository.findFirstByIdGreaterThanAndClosedOnLessThan(boundaryStart, cutoffLimit) != null);

//		periodAccountCodeSet.forEach(pacg -> {
//			log.info(pacg.toString());
//		});

//		periodAccountCodeTransactionTypeSet.forEach(pacg -> {
//			log.info(pacg.toString());
//		});
//
//		periodAccountCodeLabelValueSet.forEach(pacg -> {
//			log.info(pacg.toString());
//		});

			return summaryProcessingBoundary;
		});
	}


	public void endUnsummarizedPeriod(SummaryProcessingBoundary summaryProcessingBoundary, boolean historical) {
//		SummaryProcessingBoundary summaryProcessingBoundary = summaryProcessingBoundaryRepository.findFirstBySummaryType(SummaryProcessingBoundary.DOMAIN_ALL);
//		summaryProcessingBoundary.setLastTransactionIdProcessed(lastProcessedTransactionId);
		if (summaryProcessingBoundary != null) {
			summaryProcessingBoundaryRepository.save(summaryProcessingBoundary);
		} else {
			if (!historical)
				log.warn("Summary processing boundary is null, thus the domain summary job is not running properly");
		}
	}
	public long allocatePeriodsToExistingTransactions(long boundaryStart,
	                                                  long boundaryEnd,
	                                                  SummaryProcessingBoundary summaryProcessingBoundry,
	                                                  LinkedHashSet<PeriodAccountCodeGroup> periodAccountCodeSet,
	                                                  LinkedHashSet<PeriodAccountCodeTransactionTypeGroup> periodAccountCodeTransactionTypeSet,
	                                                  LinkedHashSet<PeriodAccountCodeLabelValueGroup> periodAccountCodeLabelValueSet,
	                                                  Date cutoffLimit) throws Exception {
		long[] maxTranId = {0L};
		log.debug("Starting transaction to period linking operation boundary start:" +boundaryStart+" boundary end: "+boundaryEnd);
		metrics.timer(log).time("allocatePeriodsToExistingTransactions", (StopWatch sw) -> {

			sw.start("findIdGreaterThanAndIdLessThanEqualOrderByIdAsc_"+boundaryStart+"_"+boundaryEnd);
			List<Transaction> tranList = transactionRepository.findByIdGreaterThanAndIdLessThanEqualAndClosedOnLessThan(boundaryStart, boundaryEnd, cutoffLimit);
			log.debug("Found "+tranList.size() + " transactions to process.");
			sw.stop();

			tranList.forEach(transaction -> {
				log.trace("Start processing of transaction id: " + transaction.getId());
				long tranId = transaction.getId();
				sw.start("tranEntryList_"+tranId);
				List<TransactionEntry> tranEntryList = transactionEntryRepository.findByTransactionId(tranId);
				log.trace("Transaction entry list size of transaction id: " + transaction.getId() + " is: " + tranEntryList.size());
				sw.stop();

				sw.start("transactionLabelValueRepositoryGet_"+tranId);
				List<TransactionLabelValue> tlvList = transactionLabelValueRepository.findByTransactionId(tranId);
				// Hack. We need to introduce a new field in the transaction type label definition to specify the type
				// of summary, if any: player, domain.
				// Here, for now, we want to exclude these labels from domain summaries, as these are unique values.
				tlvList.removeIf(tlv -> {
					return TransactionService.PERIOD_DOMAIN_SUMMARY_EXCLUDED_LABELS.contains(
							tlv.getLabelValue().getLabel().getName());
				});
				log.trace("Transaction label value list size of transaction id: " + transaction.getId() + " is: " + tlvList.size());
				sw.stop();
				List<Period> entryPeriodList = new ArrayList<>(5);
				tranEntryList.forEach(entry -> {

					sw.start("findEntryPeriods_"+entry.getId());
						findEntryPeriods(entry, entryPeriodList);
					log.trace("Transaction entry period size of transaction id: " + transaction.getId() + " entry id: " +entry.getId()+ " is: " + entryPeriodList.size());
					sw.stop();

					sw.start("addPeriodAccountCodeGroupsToSet");
					log.trace("Hash set sizes before adding periods of : " + transaction.getId() + " entry id: " +entry.getId()+
							" periodAccountCodeSet: " + periodAccountCodeSet.size() +
							" periodAccountCodeTransactionTypeSet: " + periodAccountCodeTransactionTypeSet.size() +
							" periodAccountCodeLabelValueSet: "+ periodAccountCodeLabelValueSet.size());
					entryPeriodList.forEach(period -> {

						//If current tran id > what was last processed it means it is not only label value update, so include in other period hashes
						if (tranId > summaryProcessingBoundry.getLastTransactionIdProcessed()) {
							periodAccountCodeSet.add(PeriodAccountCodeGroup.builder()
									.accountCode(entry.getAccount().getAccountCode())
									.currency(entry.getAccount().getCurrency())
									.period(period)
									.build());

							periodAccountCodeTransactionTypeSet.add(PeriodAccountCodeTransactionTypeGroup.builder()
									.accountCode(entry.getAccount().getAccountCode())
									.transactionType(transaction.getTransactionType())
									.currency(entry.getAccount().getCurrency())
									.period(period)
									.build());
						}

						tlvList.forEach(transactionLabelValue -> {
							periodAccountCodeLabelValueSet.add(PeriodAccountCodeLabelValueGroup.builder()
									.accountCode(entry.getAccount().getAccountCode())
									.transactionType(transaction.getTransactionType())
									.currency(entry.getAccount().getCurrency())
									.period(period)
									.labelValue(transactionLabelValue.getLabelValue())
									.build());

							if (summaryProcessingBoundry.getLastTransactionLabelValueIdProcessed() < transactionLabelValue.getId())
								summaryProcessingBoundry.setLastTransactionLabelValueIdProcessed(transactionLabelValue.getId());
						});
					});
					log.trace("Hash set sizes after adding periods of : " + transaction.getId() + " entry id: " +entry.getId()+
							" periodAccountCodeSet: " + periodAccountCodeSet.size() +
							" periodAccountCodeTransactionTypeSet: " + periodAccountCodeTransactionTypeSet.size() +
							" periodAccountCodeLabelValueSet: "+ periodAccountCodeLabelValueSet.size());
					sw.stop();

				});
				if (summaryProcessingBoundry.getLastTransactionIdProcessed() <= tranId) {
					summaryProcessingBoundry.setLastTransactionIdProcessed(tranId);
					maxTranId[0] = tranId;
				}
			});

		});
		log.debug("Completed transaction to period linking operation: maxTranId=" + maxTranId[0]);
		return maxTranId[0];
	}

	private void findEntryPeriods(TransactionEntry entry, List<Period> periodList) {
		if (!periodList.isEmpty()) return;
		for (int granularity = 1; granularity <= 5; granularity++) {
			try {
				Period period = periodService.findOrCreatePeriod(new DateTime(entry.getDate().getTime()), entry.getAccount().getDomain(), granularity);
				// NEVERMIND This will not work for only closed periods. There will be ear and all time cycles that will take long to update to correct value
				//Check if the period is technically closed and include it in the evaluation if so
				// There is a very minute chance of a period starting to fall into the closed category
				// while live running is still happening but it will not impact final outcome
//				if (onlyClosedPeriods) {
//					if (period.getDateEnd().before(DateTime.now().toDate()) || granularity == 5) {
//						periodList.add(period);
//					}
//				} else {
					periodList.add(period);
				//}
			} catch (Exception ex) {
				log.error("Problem getting periods for entry: " + entry, ex);
			}
		}
	}

	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.REQUIRED)
	public void calculateDamaged(LinkedHashSet<PeriodAccountCodeGroup> periodAccountCodeSet) {
		for (PeriodAccountCodeGroup pacg: periodAccountCodeSet) {
			SummaryAccountGroup summaryAccountGroup = summaryAccountRepository.groupBy(pacg.getPeriod(), pacg.getAccountCode(), pacg.getCurrency());
			calculate(summaryAccountGroup);
		}
	}

	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.REQUIRED)
	public void calculate(Period period) {
		log.debug(this.getClass().getSimpleName() + " calculate " + period);
		repository.updateTag(period, 1);

		List<SummaryAccountGroup> summaryAccountGroups = summaryAccountRepository.groupBy(period);
		for (SummaryAccountGroup summaryAccountGroup: summaryAccountGroups) {
			calculate(summaryAccountGroup);
		}

		repository.deleteByPeriodAndTag(period, 1);
	}

	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.REQUIRED)
	public void calculate(SummaryAccountGroup group) {
		log.debug(this.getClass().getSimpleName() + " calculate " + group);
		SummaryDomain summary = repository.findByPeriodAndAccountCodeAndCurrency(
				group.getPeriod(),  group.getAccountCode(), group.getCurrency());
		if (summary == null) {
			summary = SummaryDomain.builder()
					.accountCode(group.getAccountCode())
					.currency(group.getCurrency())
					.period(group.getPeriod())
					.build();
		}
		summary.setTranCount(group.getTranCount());
		summary.setOpeningBalanceCents(group.getOpeningBalanceCents());
		summary.setDebitCents(group.getDebitCents());
		summary.setCreditCents(group.getCreditCents());
		summary.setClosingBalanceCents(group.getClosingBalanceCents());
		summary.setTag(2);
		repository.save(summary);
	}
}