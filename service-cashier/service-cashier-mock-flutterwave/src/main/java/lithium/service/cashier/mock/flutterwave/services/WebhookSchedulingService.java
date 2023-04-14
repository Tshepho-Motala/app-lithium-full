package lithium.service.cashier.mock.flutterwave.services;

import lithium.leader.LeaderCandidate;
import lithium.service.cashier.mock.flutterwave.data.entities.FlutterwaveTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WebhookSchedulingService {
    @Autowired
    private LeaderCandidate leaderCandidate;

    @Autowired
    private PaymentService service;

    public void processPendingTransactions() {
        List<FlutterwaveTransaction> transactions = service.getFinalizedTransactions();
        transactions.forEach(flutterwaveTransaction -> service.simulateWebhook(flutterwaveTransaction));
    }

    public void processUpdateTransactions() {
        List<FlutterwaveTransaction> transactions = service.getPendingTransactions();
        transactions.forEach(flutterwaveTransaction -> service.updateTransactionStatus(flutterwaveTransaction));
    }
}
