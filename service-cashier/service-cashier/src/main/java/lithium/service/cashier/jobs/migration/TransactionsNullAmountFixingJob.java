package lithium.service.cashier.jobs.migration;

import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionData;
import lithium.service.cashier.data.repositories.TransactionDataRepository;
import lithium.service.cashier.data.repositories.TransactionRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class TransactionsNullAmountFixingJob {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionDataRepository transactionDataRepository;

    private long total;
    private AtomicLong updatedTransactionsCount;

    public void migrateTransactionsAmountFix(int pageSize, Long delay) throws InterruptedException {
        log.info("TransactionsNullAmount Migration job requested with: pageSize={} delay={} isMigrationJobStarted={}", pageSize, delay, JobState.isStarted);
        if (!JobState.isStarted) {
            updatedTransactionsCount = new AtomicLong(0);
            total = transactionRepository.countByAmountCentsIsNullAndStatusActiveFalse();
            JobState.start();
            Pageable page = PageRequest.of(0, pageSize);
            List<Transaction> transactionsNeedToUpdate = transactionRepository.findByAmountCentsIsNullAndStatusActiveFalse(page);
            log.info("TransactionsNullAmount Migration started for:" + total + " transactions");

            do {
                if (JobState.isTerminated) break;
                try {
                    migrate(transactionsNeedToUpdate);
                } catch (Exception e) {
                    log.error("TransactionsNullAmount Migration job cannot continue because:" + e.getMessage() + ". Terminating migration job", e);
                    JobState.terminate();
                    break;
                }
                throttleMigration(delay);
                transactionsNeedToUpdate = transactionRepository.findByAmountCentsIsNullAndStatusActiveFalse(page);

            } while (transactionsNeedToUpdate.size() > 0);

            JobState.finish();
            log.info(":: TransactionsNullAmount Migration job is finished." + updatedTransactionsCount.get() + " transactions precessed");
        } else {
            log.info("Migration is running:" + updatedTransactionsCount.get() + " of " + total + " completed");
        }
    }

    private void migrate(List<Transaction> transactionsNeedToUpdate) {
        transactionsNeedToUpdate.forEach(transaction -> {
            String strAmount = Optional.ofNullable(transactionDataRepository.findByTransactionAndFieldAndStageAndOutput(transaction, "amount", 1, false))
                    .map(TransactionData::getValue)
                    .orElse("0");
            try {
                long amountCents = new BigDecimal(strAmount).movePointRight(2).longValue();
                transaction.setAmountCents(amountCents);
                transactionRepository.save(transaction);
                updatedTransactionsCount.getAndIncrement();
                log.info("Fixed null amountCents to " + amountCents + " for transaction id=" + transaction.getId());
            } catch (Exception ex) {
                log.error("Unknown amount format " + strAmount + " for transaction id=" + transaction.getId(), ex);
            }
            log.info("Successfully updated null amount for " + updatedTransactionsCount.get() + "of" + total + " transactions");
        });
    }

    public long getTotal() {
        return total;
    }

    public long getUpdatedTransactionsCount() {
        return updatedTransactionsCount.get();
    }

    private void throttleMigration(Long delay) throws InterruptedException {
        Thread.sleep(delay);
    }

    public void terminate() {
        JobState.terminate();
        log.info("TransactionsNullAmount MigrationJob is terminated with " + updatedTransactionsCount.get() + " of " + total + " transactions processed");
        updatedTransactionsCount = new AtomicLong(0);
        total = 0L;
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
