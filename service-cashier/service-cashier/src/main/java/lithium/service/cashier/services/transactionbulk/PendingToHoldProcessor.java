package lithium.service.cashier.services.transactionbulk;

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
public class PendingToHoldProcessor implements TransactionBulkProcessor{

	private final WebApplicationContext beanContext;
	private final TransactionService transactionService;

	@Override
	public Integer proceed(String guid, String comment, LithiumTokenUtil token) {
		List<Transaction> transactions = transactionService.getUsersTransactionsByCodes(guid, TransactionStatus.getPengingStatusCodes());
		try {
			for(Transaction transaction : transactions) {
                runOnHold(comment, token, transaction);
            }
		} catch (Exception e) {
			log.error("Failed to move withdrawals on hold for guid=" + guid + " :" + e.getMessage(), e);
			return -1;
		}
		return transactions.size();
	}

    private void runOnHold(String comment, LithiumTokenUtil token, Transaction transaction) throws Exception {
        DoMachine machine = beanContext.getBean(DoMachine.class);
        machine.onHold(transaction.getUser().domainName(), transaction.getId(), comment, token);
    }

    @Override
	public TransactionProcessingCode getCode() {
		return TransactionProcessingCode.HOLD_PENDING_WITHDRAWALS;
	}

    @Override
    public BulkResult proceed(List<Long> transactionIds, String comment, LithiumTokenUtil token) {
        List<Long> proceedIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        transactionIds.forEach(id -> {
            Transaction transaction = transactionService.getTransactionById(id);
            try {
                runOnHold(comment, token, transaction);
                proceedIds.add(id);
            } catch (Exception e) {
                log.error("Proceed onHold operation failed for transaction id = "+id, e);
                failedIds.add(id);
            }
        });
        return BulkResult.builder()
                .proceedIds(proceedIds)
                .failedIds(failedIds)
                .build();
    }
}