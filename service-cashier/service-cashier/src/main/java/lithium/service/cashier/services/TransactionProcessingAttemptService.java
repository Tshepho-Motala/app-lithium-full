package lithium.service.cashier.services;

import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionProcessingAttempt;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;
import lithium.service.cashier.data.repositories.TransactionProcessingAttemptRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionProcessingAttemptService {
    private final TransactionProcessingAttemptRepository processingAttemptRepo;

    public TransactionProcessingAttempt saveProcessingAttempt(Transaction t, boolean success, String messages, String rawRequest, String rawResponse, String reference, TransactionWorkflowHistory from, TransactionWorkflowHistory to) {

        log.info("saveProcessingAttempt transactionId " + t.getId()
                + " success " + success + " messages " + messages + " rawResponse " + rawResponse + " reference " + reference);

        TransactionProcessingAttempt a = TransactionProcessingAttempt.builder()
                .transaction(t)
                .workflowFrom(from)
                .workflowTo(to)
                .success(success)
                .processorMessages(messages)
                .processorReference(reference)
                .processorRawRequest(rawRequest)
                .processorRawResponse(rawResponse)
                .build();

        processingAttemptRepo.save(a);

        return a;
    }

    public List<TransactionProcessingAttempt> attempts(Transaction transaction) {
        return processingAttemptRepo.findByTransactionOrderByTimestampDesc(transaction);
    }
    public TransactionProcessingAttempt attempt(Transaction transaction, TransactionWorkflowHistory transactionWorkflowHistory) {
        return processingAttemptRepo.findByTransactionAndWorkflowTo(transaction, transactionWorkflowHistory);
    }
    public TransactionProcessingAttempt lastAttempt(Transaction transaction) {
        return processingAttemptRepo.findTopByTransactionOrderByIdDesc(transaction);
    }


    public List<TransactionProcessingAttempt> findProcessingAttemptForCleanup(int page, int batchSize, LocalDate localDate) {
        PageRequest pageRequest = PageRequest.of(page, batchSize, Sort.by(Sort.Direction.ASC, "transaction_id", "id"));
        Date date = Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        return processingAttemptRepo.findAllByTimestampBeforeAndCleanedFalse(date, pageRequest);
    }

    public void saveProcessingAttempts(List<TransactionProcessingAttempt> processingAttemptForCleanup) {
        processingAttemptRepo.saveAll(processingAttemptForCleanup);
    }
}
