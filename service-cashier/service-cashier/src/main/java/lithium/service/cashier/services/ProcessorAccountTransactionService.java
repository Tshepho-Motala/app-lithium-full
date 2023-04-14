package lithium.service.cashier.services;

import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.objects.ProcessorAccountTransactionState;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.ProcessorAccountTransaction;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.ProcessorAccountTransactionRepository;
import lithium.service.cashier.data.repositories.ProcessorAccountTransactionStateRepository;
import lithium.service.cashier.data.repositories.ProcessorUserCardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class ProcessorAccountTransactionService {

	@Autowired
	ProcessorAccountTransactionRepository paTransactionRepository;

	@Autowired
	ProcessorUserCardRepository processorUserCardRepository;
	@Autowired
	ProcessorAccountTransactionStateRepository paTransactionStateRepository;

	public ProcessorAccountTransaction createTransaction(User user, DomainMethodProcessor domainMethodProcessor, String redirectUrl) {
		ProcessorAccountTransaction t = ProcessorAccountTransaction.builder()
				.createdOn(new Date())
				.domainMethodProcessor(domainMethodProcessor)
				.user(user)
				.state(paTransactionStateRepository.findByName(ProcessorAccountTransactionState.CREATED.getName()))
				.redirectUrl(redirectUrl)
				.build();

		return paTransactionRepository.save(t);
	}

	public ProcessorAccountTransaction updateTransaction(Long transactionId, String processorReference, ProcessorAccountTransactionState state, Long processorAccountId, String errorCode, String errorMessage, String generalError) {
		ProcessorAccountTransaction transaction = paTransactionRepository.findOne(transactionId);
		boolean saveTransaction = false;
		if (processorReference != null && !processorReference.equalsIgnoreCase(transaction.getProcessorReference())) {
			saveTransaction = true;
			transaction.setProcessorReference(processorReference);
		}
		if (state != null && (transaction.getState() == null || state != ProcessorAccountTransactionState.fromName(transaction.getState().getName()))) {
			saveTransaction = true;
			transaction.setState(paTransactionStateRepository.findByName(state.getName()));
		}
		if (errorCode != null && !errorCode.equalsIgnoreCase(transaction.getErrorCode())) {
			saveTransaction = true;
			transaction.setErrorCode(errorCode);
		}
		if (errorMessage != null && !errorMessage.equalsIgnoreCase(transaction.getErrorMessage())) {
			saveTransaction = true;
			transaction.setErrorMessage(errorMessage);
		}
		if (generalError != null && !generalError.equalsIgnoreCase(transaction.getGeneralError())) {
			saveTransaction = true;
			transaction.setGeneralError(generalError);
		}

		if (processorAccountId != null && transaction.getProcessorAccount() == null) {
			saveTransaction = true;
			transaction.setProcessorAccount(processorUserCardRepository.findOne(processorAccountId));
		}

		return saveTransaction ? paTransactionRepository.save(transaction) : transaction;
	}

	public ProcessorAccountTransaction updateTransaction(Long transactionId, ProcessorAccountResponse response, Long processorAccountId) {
		ProcessorAccountTransactionState state = null;
		switch (response.getStatus()) {
			case SUCCESS:
				state = ProcessorAccountTransactionState.SUCCESS;
				break;
			case FAILED:
				state = ProcessorAccountTransactionState.FAILED;
				break;
			case PENDING:
				state = ProcessorAccountTransactionState.PENDING;
				break;
			case CANCELED:
				state = ProcessorAccountTransactionState.CANCELED;
				break;
			default:
				state = null;
				break;
		}
		return updateTransaction(transactionId, response.getProcessorReference(), state, processorAccountId, response.getErrorCode(), response.getErrorMessage(), response.getGeneralError());
	}

	public ProcessorAccountTransaction getTransactionById(Long transactionId) {
		return paTransactionRepository.findOne(transactionId);
	}
}
