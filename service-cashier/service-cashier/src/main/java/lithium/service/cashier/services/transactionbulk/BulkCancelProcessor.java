package lithium.service.cashier.services.transactionbulk;

import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.objects.BulkResult;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.cashier.services.TransactionService;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BulkCancelProcessor  implements TransactionBulkProcessor {

    private final WebApplicationContext beanContext;
    private final TransactionService transactionService;
    @Override
    public Integer proceed(String guid, String comment, LithiumTokenUtil token) {
        List<Transaction> transactions = transactionService.getUsersTransactionsByCodes(guid, TransactionStatus.getPengingStatusCodes());
        try {
            for(Transaction transaction : transactions) {
                doMachineCancel(transaction.getId(), comment);
            }
        } catch (Exception e) {
            log.error("Failed to move withdrawals on hold for guid=" + guid + " :" + e.getMessage(), e);
            return -1;
        }
        return transactions.size();
    }

    @Override
    public TransactionProcessingCode getCode() {
        return TransactionProcessingCode.CANCEL;
    }

    @Override
    public BulkResult proceed(List<Long> transactionIds, String comment, LithiumTokenUtil token) {
        List<Long> proceedIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        transactionIds.forEach(id ->
            executeCancel(comment, proceedIds, failedIds, id)
        );
        return BulkResult.builder()
                .proceedIds(proceedIds)
                .failedIds(failedIds)
                .build();
    }

    private void executeCancel(String comment, List<Long> proceedIds, List<Long> failedIds, Long id) {
        try {
            DoResponse response = doMachineCancel(id, comment);
            if (!"CANCEL".equalsIgnoreCase(response.getState())) {
                failedIds.add(id);
            } else {
                proceedIds.add(id);
            }
        } catch (Exception e) {
            log.error("Proceed Cancel operation failed for transaction id = "+ id, e);
            failedIds.add(id);
        }
    }

    private DoResponse doMachineCancel(Long id, String comment) throws Exception {
        DoMachine machine = beanContext.getBean(DoMachine.class);
        return machine.cancel(id, comment);
    }
}
