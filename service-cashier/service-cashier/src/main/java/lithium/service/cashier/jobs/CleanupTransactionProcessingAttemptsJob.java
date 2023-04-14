package lithium.service.cashier.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.cashier.data.entities.TransactionProcessingAttempt;
import lithium.service.cashier.jobs.config.CleanupTransactionProcessingAttemptsJobConfig;
import lithium.service.cashier.services.TransactionProcessingAttemptService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class CleanupTransactionProcessingAttemptsJob {

	private final LeaderCandidate leaderCandidate;
	private final TransactionProcessingAttemptService processingAttemptService;
	private final CleanupTransactionProcessingAttemptsJobConfig config;

	@Scheduled(cron="${lithium.service.cashier.jobs.cleanup-transaction-processing-attempt.cron: 0 0 4 * * *}")
	public void checkForCleanup() {
		//Leadership
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		log.info("Starting CleanupTransactionProcessingAttemptsJob...");
		int page = 0;
		List<TransactionProcessingAttempt> processingAttemptForCleanup;
		do {
			processingAttemptForCleanup = processingAttemptService.findProcessingAttemptForCleanup(page++, config.getBatchSize(), LocalDate.now().minusDays(config.getCleanupRetentionInDays()));
			if (processingAttemptForCleanup.size() > 0) {
				processingAttemptForCleanup.forEach(cleanupAttempt());
				processingAttemptService.saveProcessingAttempts(processingAttemptForCleanup);
				Map<Long, List<Long>> cleanedTransaction = processingAttemptForCleanup.stream().collect(Collectors.groupingBy(transactionProcessingAttempt -> transactionProcessingAttempt.getTransaction().getId(), Collectors.mapping(TransactionProcessingAttempt::getId, Collectors.toList())));
				log.info("Transaction processing attempts logs cleaned: " + cleanedTransaction);
			}
			if (config.getPageCountPerOnce() > 0 && config.getPageCountPerOnce() == page) {
				log.info("Page amount limit reached. Exiting... ");
				return;
			}
		} while (processingAttemptForCleanup.size() == config.getBatchSize());

	}

	private Consumer<TransactionProcessingAttempt> cleanupAttempt() {
		return transactionProcessingAttempt -> {
			transactionProcessingAttempt.setProcessorRawRequest("");
			transactionProcessingAttempt.setProcessorRawResponse("");
			transactionProcessingAttempt.setCleaned(true);
		};
	}

}
