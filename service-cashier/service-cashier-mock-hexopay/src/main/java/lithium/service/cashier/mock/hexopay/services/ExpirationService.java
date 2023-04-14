package lithium.service.cashier.mock.hexopay.services;

import lithium.service.cashier.mock.hexopay.data.entities.Transaction;
import lithium.service.cashier.mock.hexopay.data.entities.TransactionToken;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static lithium.service.cashier.mock.hexopay.data.Scenario.NO_NOTIFICATION_DELAY;

@Service
@Slf4j
public class ExpirationService {
    @Autowired
    TaskScheduler taskScheduler;
    @Autowired
    TransactionService transactionService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    Simulator simulator;

    @Scheduled(fixedDelayString = "${lithium.services.cashier.mock.hexopay.expire-scheduling-time:2000}")
    public void expireTransactions() {
        List<Transaction> transactions = transactionService.getTransactionToExpire();
        final Date now = new Date();
        transactions.stream().forEach(transaction -> {
            if (now.getTime() > transaction.getTtl()) {
                transaction = transactionService.updateTransaction(transaction.getUid(), Status.expired, "Transaction is expired", -1L);
                notificationService.notify(simulator.mapTransaction(transaction), transaction.getNotificationUrl(), NO_NOTIFICATION_DELAY);
            }
        });

        List<TransactionToken> transactionTokens = transactionService.getTransactionTokenToExpire();
        transactionTokens.stream().forEach(token -> {
            if (now.getTime() > token.getTtl()) {
                transactionService.updateTransactionToken(token.getToken(), Status.expired, -1L, null);
            }
        });
    }
}
