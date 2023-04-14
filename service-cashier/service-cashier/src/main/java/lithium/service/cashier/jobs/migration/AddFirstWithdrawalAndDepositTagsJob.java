package lithium.service.cashier.jobs.migration;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.enums.TransactionTagType;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.UserRepository;
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
public class AddFirstWithdrawalAndDepositTagsJob {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserRepository userRepository;

    private long totalCashierUserCount;
    private AtomicLong updatedUsersCount;

    @Async
    public void startJob(int pageSize, Long delay, Integer startPage) throws InterruptedException {
        log.info("FirstWithdrawalAndDepositTags job requested with: pageSize={} delay={} isJobStarted={}", pageSize, delay, JobState.isStarted);

        if (!JobState.isStarted) {
            JobState.start();
            Pageable page = PageRequest.of(startPage, pageSize).withSort(Sort.by(Sort.Direction.ASC, "id"));
            updatedUsersCount = new AtomicLong(0L);
            totalCashierUserCount = userRepository.count();
            log.info("FirstWithdrawalAndDepositTags job is started for total: " + totalCashierUserCount + " users");
            Page<User> userList = Page.empty();

            do {
                if (!JobState.isTerminated) {
                    userList = userRepository.findAll(page);
                    userList.getContent().parallelStream().forEach(this::addTagsForUserTransactions);
                    log.info("FirstWithdrawalAndDepositTags job is running " + getUpdatedUsersCount() + " of " + totalCashierUserCount + " updated");
                    throttleJob(delay);
                    page = page.next();
                }

            } while (userList.hasNext());

            log.info("FirstWithdrawalAndDepositTags job is finished");
            JobState.finish();

        } else {
            log.info("FirstWithdrawalAndDepositTags job is running " + getUpdatedUsersCount() + " of " + totalCashierUserCount + " updated");
        }
    }

    private void addTagsForUserTransactions(User user) {

        Transaction firstDepositTransaction = transactionService.findFirstTransaction(user.getGuid(), TransactionType.DEPOSIT, DoMachineState.SUCCESS.name());

        if (firstDepositTransaction != null
                && firstDepositTransaction.getTags().stream()
                .noneMatch(transactionTag -> transactionTag.getType().getName().equalsIgnoreCase(TransactionTagType.FIRST_DEPOSIT.getName()))) {
            transactionService.addTagForTransaction(firstDepositTransaction, TransactionTagType.FIRST_DEPOSIT);
        }

        Transaction firstWithdrawalTransaction = transactionService.findFirstTransaction(user.getGuid(), TransactionType.WITHDRAWAL, DoMachineState.SUCCESS.name());

        if (firstWithdrawalTransaction != null
                && firstWithdrawalTransaction.getTags().stream()
                .noneMatch(transactionTag -> transactionTag.getType().getName().equalsIgnoreCase(TransactionTagType.FIRST_WITHDRAWAL.getName()))) {
            transactionService.addTagForTransaction(firstWithdrawalTransaction, TransactionTagType.FIRST_WITHDRAWAL);
        }

        updatedUsersCount.getAndIncrement();
    }

    private void throttleJob(Long delay) throws InterruptedException {
        Thread.sleep(delay);
    }

    public Long getTotal() {
        return totalCashierUserCount;
    }

    public Long getUpdatedUsersCount() {
        if (updatedUsersCount != null) {
            return updatedUsersCount.get();
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
