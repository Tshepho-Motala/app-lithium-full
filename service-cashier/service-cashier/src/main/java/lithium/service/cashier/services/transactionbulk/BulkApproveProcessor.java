package lithium.service.cashier.services.transactionbulk;


import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.objects.BulkResult;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.cashier.services.TransactionService;
import lithium.service.cashier.services.transactionbulk.TransactionBulkProcessor;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BulkApproveProcessor implements TransactionBulkProcessor {

    private final TransactionService transactionService;
    private final WebApplicationContext beanContext;
    @Override
    public Integer proceed(String guid, String comment, LithiumTokenUtil token) {
        List<Transaction> transactions = transactionService.getUsersTransactionsByCodes(guid, Arrays.asList("WAITFORAPPROVAL"));
        try {
            for(Transaction transaction : transactions) {
                processDelayedApprove(transaction.getId(), comment, token);
            }
        } catch (Exception e) {
            log.error("Failed to bulk approve withdrawals for guid=" + guid + " :" + e.getMessage(), e);
            return -1;
        }
        return transactions.size();
    }

    @Override
    public TransactionProcessingCode getCode() {
        return TransactionProcessingCode.APPROVE_WITHDRAWALS;
    }

    @Override
    public BulkResult proceed(List<Long> transactionIds, String comment, LithiumTokenUtil token) {
        List<Long> approvedIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();

        transactionIds.forEach(id -> {
                    if (processDelayedApprove(id, comment, token)) {
                        approvedIds.add(id);
                    } else {
                        failedIds.add(id);
                    }
                }
        );

        return BulkResult.builder()
                .proceedIds(approvedIds)
                .failedIds(failedIds)
                .build();
    }

    private boolean processDelayedApprove(Long id, String comment, LithiumTokenUtil token) {
        Transaction transaction = transactionService.getTransactionById(id);
        if (transaction == null || !transaction.getStatus().isAbleToApprove()) return false;
        try {
            DoMachine machine = beanContext.getBean(DoMachine.class);
            DoResponse response = machine.processDelayedApprove(transaction.getUser().domainName(), transaction.getId(), comment ,token);
            return response.getError() == null ? true : !response.getError();
        } catch (Exception e) {
            log.error("Failed to bulk approve withdrawal from transaction id=" + id + " :" + e.getMessage(), e);
            return false;
        }
    }
}
