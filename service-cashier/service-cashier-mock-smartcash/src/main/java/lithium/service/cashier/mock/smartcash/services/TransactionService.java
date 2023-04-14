package lithium.service.cashier.mock.smartcash.services;

import lithium.service.cashier.mock.smartcash.data.Scenario;
import lithium.service.cashier.mock.smartcash.data.entities.Customer;
import lithium.service.cashier.mock.smartcash.data.entities.Transaction;
import lithium.service.cashier.mock.smartcash.data.repositories.TransactionRepository;
import lithium.service.cashier.processor.smartcash.data.enums.SmartcashTransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction createTransaction(String type, String amount, String country, String currency, SmartcashTransactionStatus status, String message,
                                         String reference, Scenario scenario, Customer customer) {

        Transaction transaction = Transaction.builder()
                .customer(customer)
                .reference(reference)
                .amount(amount)
                .currency(currency)
                .country(country)
                .type(type)
                .status(status)
                .message(message)
                .scenario(scenario)
                .build();

        return transactionRepository.save(transaction);
    }

    public Transaction getByReference(String reference) {
        return transactionRepository.findByReference(reference);
    }
    private String getUUID() {
        return "mock_" + UUID.randomUUID().toString();
    }
}
