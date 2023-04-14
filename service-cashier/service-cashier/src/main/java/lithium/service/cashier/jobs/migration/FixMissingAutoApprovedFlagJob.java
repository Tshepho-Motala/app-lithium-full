package lithium.service.cashier.jobs.migration;

import lithium.service.cashier.client.objects.enums.TransactionTagType;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;
import lithium.service.cashier.data.repositories.TransactionWorkflowHistoryRepository;
import lithium.service.cashier.services.TransactionService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class FixMissingAutoApprovedFlagJob {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionWorkflowHistoryRepository workflowHistoryRepository;

    private long totalCashierUserCount;
    private AtomicLong updatedTransactionsCount;

    @Async
    public void startJob(int pageSize, Long delay, Integer startPage) throws InterruptedException {
        log.info("Requested launch with: pageSize={} delay={} isJobStarted={}", pageSize, delay, JobState.isStarted);

        if (!JobState.isStarted) {
            JobState.start();
            Pageable page = PageRequest.of(startPage, pageSize).withSort(Sort.by(Sort.Direction.ASC, "id"));
            updatedTransactionsCount = new AtomicLong(0L);
            totalCashierUserCount = workflowHistoryRepository.countByStatusCode("AUTO_APPROVED");
            log.info("Job is started for total: " + totalCashierUserCount + " transactions");
            Page<TransactionWorkflowHistory> transactions = Page.empty();

            do {
                if (!JobState.isTerminated) {
                    transactions = workflowHistoryRepository.findAllByStatusCode("AUTO_APPROVED", page);
                    transactions.getContent().forEach(this::addTagsForUserTransactions);
                    log.info("Missing auto-approved flag updated for " + getUpdatedTransactionsCount() + " of " + totalCashierUserCount);
                    throttleJob(delay);
                    page = page.next();
                }

            } while (transactions.hasNext());

            log.info("Job finished");
            JobState.finish();

        } else {
            log.info("Job is running " + getUpdatedTransactionsCount() + " of " + totalCashierUserCount + " updated");
        }
    }

    private void addTagsForUserTransactions(TransactionWorkflowHistory workflowHistory) {
        transactionService.addTagForTransaction(workflowHistory.getTransaction(), TransactionTagType.AUTO_APPROVED);
        updatedTransactionsCount.getAndIncrement();
    }

    private void throttleJob(Long delay) throws InterruptedException {
        Thread.sleep(delay);
    }

    public Long getTotal() {
        return totalCashierUserCount;
    }

    public Long getUpdatedTransactionsCount() {
        if (updatedTransactionsCount != null) {
            return updatedTransactionsCount.get();
        } else return 0L;
    }

    public void terminate() {
        log.info("FirstDepositAndWithdrawalTags update job is manually terminated");
        JobState.terminate();
    }

    @Getter
    private static class JobState {
        private static boolean isStarted;
        private static boolean isTerminated;

        public static void start() {
            isStarted = true;
            isTerminated = false;
        }

        public static void terminate() {
            isTerminated = true;
            isStarted = false;
        }

        private static void finish() {
            isStarted = false;
        }
    }

}
