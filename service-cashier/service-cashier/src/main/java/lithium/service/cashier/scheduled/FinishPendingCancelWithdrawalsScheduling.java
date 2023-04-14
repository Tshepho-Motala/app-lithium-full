package lithium.service.cashier.scheduled;

import lithium.leader.LeaderCandidate;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.cashier.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@Slf4j
@Service
public class FinishPendingCancelWithdrawalsScheduling {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private WebApplicationContext beanContext;
    @Autowired
    private LeaderCandidate leaderCandidate;

    @Scheduled(initialDelayString = "${lithium.services.cashier.finish-pending-cancel-delay-ms:180000}",
            fixedRateString = "${lithium.services.cashier.finish-pending-cancel-interval-ms:120000}")
    public void finishPendingCancelWithdrawals() {
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }
        log.info("Look for withdrawals in pending-cancel state to finish...");
        List<Transaction> transactions = transactionService.findByTransactionTypeAndStatusCode(TransactionType.WITHDRAWAL, DoMachineState.PENDING_CANCEL.name());
        for (Transaction t : transactions) {
            log.info("Finishing transaction in pending-cancel state : " + t.getId() + ", " + t.getUser().guid());
            try {
                DoMachine machine = beanContext.getBean(DoMachine.class);
                machine.cancel(t.getId(), "Finish PendingCancelWithdrawals Scheduling");
            } catch (Exception e) {
                log.error("Got error during finish transactions in pending-cancel state", e);
            }
        }

    }
}