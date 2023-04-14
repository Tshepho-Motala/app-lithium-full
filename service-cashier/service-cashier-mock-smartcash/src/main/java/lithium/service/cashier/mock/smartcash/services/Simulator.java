package lithium.service.cashier.mock.smartcash.services;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.mock.smartcash.data.Scenario;
import lithium.service.cashier.mock.smartcash.data.entities.Customer;
import lithium.service.cashier.mock.smartcash.data.entities.Transaction;
import lithium.service.cashier.mock.smartcash.data.exceptions.SmartcashMockException;
import lithium.service.cashier.mock.smartcash.data.exceptions.SmartcashStatusMessageException;
import lithium.service.cashier.mock.smartcash.data.exceptions.SmartcashStatusResponseException;
import lithium.service.cashier.mock.smartcash.data.repositories.CustomerRepository;
import lithium.service.cashier.processor.smartcash.data.PaymentResponseData;
import lithium.service.cashier.processor.smartcash.data.SmartcashCustomerData;
import lithium.service.cashier.processor.smartcash.data.SmartcashCustomerSearchResponse;
import lithium.service.cashier.processor.smartcash.data.SmartcashInstrument;
import lithium.service.cashier.processor.smartcash.data.SmartcashPaymentResponse;
import lithium.service.cashier.processor.smartcash.data.SmartcashResponseStatus;
import lithium.service.cashier.processor.smartcash.data.TransactionResponseData;
import lithium.service.cashier.processor.smartcash.data.enums.SmartcashTransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lithium.service.cashier.processor.smartcash.data.enums.SmartcashResponseCodes.COLLECTION_BAD_REQUEST;

@Service
@Slf4j
public class Simulator {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    LithiumConfigurationProperties config;
    @Autowired
    NotificationService notificationService;

    @Value("${spring.application.name}")
    private String moduleName;

    public SmartcashCustomerSearchResponse getCustomer(String msisdn) throws SmartcashMockException {
        return Optional.ofNullable(customerRepository.findByMsisdn(msisdn))
            .map(c -> SmartcashCustomerSearchResponse.builder()
                .data(SmartcashCustomerData.builder()
                    .instruments(new SmartcashInstrument[]{ SmartcashInstrument.builder().type("WALLET").walletId(c.getWalletId()).build() } )
                    .lastName(c.getLastName())
                    .firstName(c.getFirstName())
                    .msisdn(c.getMsisdn())
                    .middleName(c.getMiddleName())
                    .build())
                .status(SmartcashResponseStatus.builder().message("SUCCESS").success(true).code("200").build())
                .build())
            .orElse(SmartcashCustomerSearchResponse.builder()
                .status(SmartcashResponseStatus.builder().message("Customer not found.").success(false).code("400").build())
                .build());
    }

    public SmartcashPaymentResponse getTransactionStatus(String reference) throws SmartcashMockException {
        Transaction transaction = transactionService.getByReference(reference);
        if (transaction == null) {
            throw new SmartcashStatusResponseException(400, "Bad Request", COLLECTION_BAD_REQUEST.toString());
        }
        Scenario scenario = getScenario(transaction.getAmount());

        //smartcash returns 400 Bad Request on get transaction if there was no confirmation from player mobile
        if (scenario.equals(Scenario.NO_PLAYER_CONFIRMATION)) {
            throw new SmartcashStatusResponseException(400, "Bad Request", COLLECTION_BAD_REQUEST.toString());
        }

        return SmartcashPaymentResponse.builder()
            .data(PaymentResponseData.builder()
                .transaction(mapTransactionData(transaction))
                .build())
            .status(SmartcashResponseStatus.builder()
                .success(true)
                .message("SUCCESS")
                .responseCode("DP01000001001")
                .code("200")
                .build())
            .build();
    }
    public SmartcashPaymentResponse simulatePayment(String transactionId, String msisdn, String amount, String country, String currency, boolean isPayout) throws SmartcashMockException {
        Transaction transaction = transactionService.getByReference(transactionId);
        if (transaction != null) {
            throw new SmartcashStatusResponseException(400, "Transaction Already Exists", "DP01000001016");
        }

        Customer customer = customerRepository.findByMsisdn(msisdn);
        if (customer == null) {
            throw new SmartcashStatusResponseException(500, "Something Went Wrong", "DP01100001000");
        }

        Scenario scenario = getScenario(amount);
        SmartcashTransactionStatus status = SmartcashTransactionStatus.TS;
        String message = "Successfully processed";
        String statusMessage = "success";
        switch (scenario) {
            case ERROR_RESPONSE:
                throw new SmartcashStatusResponseException(500, "Something Went Wrong", "DP01100001000");
            case FAILED:
                status = SmartcashTransactionStatus.TF;
                message = "Transaction Failed";
                statusMessage = "Failed";
                break;
            case AMBIGUOUS:
                status = SmartcashTransactionStatus.TA;
                message = "Transaction Ambiguous";
                statusMessage = "Something Went Wrong";
                break;
            case INCORRECT_PIN:
                throw new SmartcashStatusMessageException(403, "pin verification failed", "ROUTER115");
            case INVALID_AMOUNT:
            case LIMIT_EXCEEDED:
            case INSUFFICIENT_FUNDS:
            case INVALID_MOBILE_NUMBER:
                throw new SmartcashStatusResponseException(400, scenario.getDescription(), scenario.getCode());
            case NO_PLAYER_CONFIRMATION:
                status = SmartcashTransactionStatus.TIP;
                message = "Transaction Is In Progress";
                break;
            case SUCCESS:
            default:
                break;
        }

        Transaction transactionEntity = transactionService.createTransaction(isPayout ? "payout" : "payment" , amount, country, currency, status, message, transactionId, scenario, customer);

        notificationService.notify(transactionEntity, scenario);

        //on deposit TIP should be returned simulating wait for player phone confirmation
        if (!isPayout && status.equals(SmartcashTransactionStatus.TS)) {
            transactionEntity.setStatus(SmartcashTransactionStatus.TIP);
            transactionEntity.setMessage("Transaction Is In Progress");
        }

        return SmartcashPaymentResponse.builder()
            .data(PaymentResponseData.builder()
                .transaction(mapTransactionData(transactionEntity))
                .build())
            .status(SmartcashResponseStatus.builder()
                .success(true)
                .message(statusMessage)
                .responseCode("DP01000001001")
                .code("200")
                .build())
            .build();
    }
    public TransactionResponseData mapTransactionData(lithium.service.cashier.mock.smartcash.data.entities.Transaction transactionEntity) {
        return TransactionResponseData.builder()
            .id(transactionEntity.getReference())
            .message(transactionEntity.getMessage())
            .status(transactionEntity.getStatus().toString())
            .smartcashMoneyId("smartcashmock_WJBT5FALCJ_" + transactionEntity.getReference())
            .timestamp(transactionEntity.getCreatedAt().toString())
            .build();
    }

    private Scenario getScenario(String amount) {
        return Scenario.getByAmount(amount);
    }

}
