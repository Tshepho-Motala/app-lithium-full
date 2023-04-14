package lithium.service.cashier.services.transactionbulk;

import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.objects.BulkResult;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.cashier.services.TransactionService;
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
public class ReprocessOnHoldProcessor implements TransactionBulkProcessor {

	private final TransactionService transactionService;
	private final WebApplicationContext beanContext;

	@Override
	public Integer proceed(String guid, String comment, LithiumTokenUtil token) {
		List<Transaction> transactions = transactionService.getUsersTransactionsByCodes(guid, Arrays.asList("ON_HOLD"));
		try {
			for(Transaction transaction : transactions) {
                runReprocess(comment, token, transaction);
            }
		} catch (Exception e) {
			log.error("Failed to reprocess withdrawals from hold for guid=" + guid + " :" + e.getMessage(), e);
			return -1;
		}
		return transactions.size();
	}

    private void runReprocess(String comment, LithiumTokenUtil token, Transaction transaction) throws Exception {
        DoMachine machine = beanContext.getBean(DoMachine.class);
        machine.reprocessOnHold(transaction.getUser().domainName(), transaction.getId(), comment, token);
    }

    @Override
	public TransactionProcessingCode getCode() {
		return TransactionProcessingCode.RE_PROCESS_ON_HOLD_WITHDRAWALS;
	}

    @Override
    public BulkResult proceed(List<Long> transactionIds, String comment, LithiumTokenUtil token) {
        List<Long> proceedIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        transactionIds.forEach(id -> {
            Transaction transaction = transactionService.getTransactionById(id);
            try {
                runReprocess(comment, token, transaction);
                proceedIds.add(id);
            } catch (Exception e) {
                log.error("Proceed reprocess operation failed for transaction id = "+id, e);
                failedIds.add(id);
            }
        });
        return BulkResult.builder()
                .proceedIds(proceedIds)
                .failedIds(failedIds)
                .build();
    }
}
