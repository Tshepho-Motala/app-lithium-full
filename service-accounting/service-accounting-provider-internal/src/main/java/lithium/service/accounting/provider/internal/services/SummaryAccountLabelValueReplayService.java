package lithium.service.accounting.provider.internal.services;

import lithium.metrics.LithiumMetricsService;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.client.stream.transactionlabel.TransactionLabelStream;
import lithium.service.accounting.objects.LabelValue;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.accounting.objects.TransactionLabelContainer;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountLabelValueReplayJob;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.entities.TransactionTypeLabel;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountLabelValueReplayJobRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionTypeLabelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SummaryAccountLabelValueReplayService {
	@Autowired private TransactionLabelStream transactionLabelStream;
	@Autowired private SummaryAccountLabelValueReplayJobRepository jobRepo;
	@Autowired private TransactionRepository tranRepo;
	@Autowired private TransactionService tranService;
	@Autowired private TransactionTypeLabelRepository tranTypeLabelRepo;
	@Autowired private LithiumMetricsService metrics;

	@Value("${lithium.service.accounting.summary.account.label-value.replay.data-fetch-size:1000}")
	private long dataFetchSize;
	@Value("${lithium.service.accounting.summary.account.label-value.replay.bound.lower:34835552}")
	private long lowerBound;
	@Value("${lithium.service.accounting.summary.account.label-value.replay.bound.upper:38208394}")
	private long upperBound;

	private static final String[] EXCLUDED_LABELS = { "device_os", "device_browser", "geo_country", "geo_state",
			"geo_city" };

	@TimeThisMethod(infoThresholdMillis = 4000, warningThresholdMillis = 4500, errorThresholdMillis = 5000)
	public void process() throws Exception {
		log.debug("SummaryAccountLabelValueReplayService.process() starting...");

		SummaryAccountLabelValueReplayJob job = jobRepo.findOne(1L);
		if (job == null) {
			job = jobRepo.save(
					SummaryAccountLabelValueReplayJob.builder()
					.id(1L)
					.build());
		}

		if (job.isProcessing()) {
			log.warn("SummaryAccountLabelValueReplayJob is still processing.");
			return;
		}

		if ((job.getCurrentId() != null) && (job.getCurrentId().longValue() >= upperBound)) {
			log.warn("SummaryAccountLabelValueReplayJob is complete. Switch"
					+ " 'lithium.service.accounting.summary.account.label-value.replay.enabled' to false.");
			return;
		}

		job.setProcessing(true);
		job = jobRepo.save(job);

		List<String> excludedLabels = Arrays.asList(EXCLUDED_LABELS);
		SW.start("tranTypeLabels");
		Map<String, TransactionTypeLabel> tranTypeLabels = getTranTypeLabels();
		SW.stop();

		long start = (job.getCurrentId() != null)
				? job.getCurrentId() + 1
				: lowerBound;
		long end = start + dataFetchSize;
		if (end > upperBound) end = upperBound;

		SW.start("transactions");
		List<Transaction> transactions = tranRepo.findByIdBetweenOrderByIdAsc(start, end);
		SW.stop();
		log.debug("Found {} transactions between {} and {}", transactions.size(), start, end);

		for (Transaction transaction: transactions) {
			SW.start("transaction_" + transaction.getId() + "_labels");
			List<LabelValue> tranLabelValues = tranService.findLabelsForTransaction(transaction.getId());
			SW.stop();
			log.debug("Found {} labels for transaction {}", tranLabelValues.size(), transaction.getId());
			SW.start("transaction_" + transaction.getId() + "_labels_filter");
			List<TransactionLabelBasic> labelList = tranLabelValues.stream()
					.filter(labelValue -> {
						String tranTypeLabelId = transaction.getTransactionType().getCode() + "-"
								+ labelValue.getLabel().getName();
						TransactionTypeLabel tranTypeLabel = tranTypeLabels.get(tranTypeLabelId);
						if ((tranTypeLabel == null) || (!tranTypeLabel.isSummarize())) {
							return false;
						}
						String tranTypeCode = transaction.getTransactionType().getCode();
						if ((tranTypeCode.contentEquals("CASHIER_DEPOSIT")) ||
								(tranTypeCode.contentEquals("CASHIER_PAYOUT"))) {
							return !excludedLabels.contains(labelValue.getLabel().getName());
						}
						return true;
					})
					.map(labelValue -> {
						return TransactionLabelBasic.builder()
								.labelName(labelValue.getLabel().getName())
								.labelValue(labelValue.getValue())
								.summarize(true)
								.build();
					}).collect(Collectors.toList());
			log.debug("Filtered to {} labels for summarization for transaction {}", labelList.size(),
					transaction.getId());
			SW.stop();

			if (!labelList.isEmpty()) {
				TransactionLabelContainer entry = TransactionLabelContainer.builder()
						.transactionId(transaction.getId())
						.labelList(labelList)
						.build();
				log.debug("Registering entry: {}", entry);
				transactionLabelStream.register(entry);
			}
			job.setCurrentId(transaction.getId());
			job = jobRepo.save(job);
		}

		if (transactions.isEmpty()) job.setCurrentId(end);
		job.setProcessing(false);
		job = jobRepo.save(job);
	}

	private Map<String, TransactionTypeLabel> getTranTypeLabels() {
		Map<String, TransactionTypeLabel> tranTypeLabels = new LinkedHashMap<>();
		tranTypeLabelRepo.findAll().iterator().forEachRemaining(transactionTypeLabel -> {
			String key = transactionTypeLabel.getTransactionType().getCode() + "-"
					+ transactionTypeLabel.getLabel();
			tranTypeLabels.computeIfAbsent(key, k -> {
				return transactionTypeLabel;
			});
		});
		log.debug("tranTypeLabels | size: {}, {}", tranTypeLabels.size(), tranTypeLabels);
		return tranTypeLabels;
	}
}
