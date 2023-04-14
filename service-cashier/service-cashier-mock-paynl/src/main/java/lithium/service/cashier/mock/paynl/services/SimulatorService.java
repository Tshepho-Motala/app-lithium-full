package lithium.service.cashier.mock.paynl.services;

import lithium.service.cashier.mock.paynl.data.enums.Scenario;
import lithium.service.cashier.mock.paynl.data.enums.WebhookAction;
import lithium.service.cashier.mock.paynl.repositories.AmountRepository;
import lithium.service.cashier.mock.paynl.repositories.CustomerRepository;
import lithium.service.cashier.mock.paynl.repositories.IBanRepository;
import lithium.service.cashier.mock.paynl.repositories.OrderRepository;
import lithium.service.cashier.mock.paynl.repositories.PaymentRepository;
import lithium.service.cashier.mock.paynl.repositories.StatsRepository;
import lithium.service.cashier.mock.paynl.repositories.TransactionRepository;
import lithium.service.cashier.processor.paynl.data.Amount;
import lithium.service.cashier.processor.paynl.data.Customer;
import lithium.service.cashier.processor.paynl.data.Stats;
import lithium.service.cashier.processor.paynl.data.Transaction;
import lithium.service.cashier.processor.paynl.data.WebhookData;
import lithium.service.cashier.processor.paynl.data.enums.TransactionStatus;
import lithium.service.cashier.processor.paynl.data.request.IBan;
import lithium.service.cashier.processor.paynl.data.request.Payment;
import lithium.service.cashier.processor.paynl.data.request.PayoutRequest;
import lithium.service.cashier.processor.paynl.data.response.BankAccount;
import lithium.service.cashier.processor.paynl.data.response.Integration;
import lithium.service.cashier.processor.paynl.data.response.Links;
import lithium.service.cashier.processor.paynl.data.response.Order;
import lithium.service.cashier.processor.paynl.data.response.PaymentMethod;
import lithium.service.cashier.processor.paynl.data.response.PayoutResponse;
import lithium.service.cashier.processor.paynl.data.response.PayoutStatusResponse;
import lithium.service.cashier.processor.paynl.data.response.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SimulatorService {

    @Value("${lithium.service.cashier.mock.paynl.delayBetweenTransactionSteps}")
    private String delayBetweenTransactionSteps;

    @Autowired
    private AmountRepository amountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private IBanRepository iBanRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private StatsRepository statsRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TaskScheduler taskScheduler;
    
    public PayoutResponse createPayout(PayoutRequest request) {
        Customer customer = request.getCustomer();
        lithium.service.cashier.mock.paynl.data.entities.Customer customerEntity = customerRepository.findByFirstNameAndLastName(customer.getFirstName(), customer.getLastName())
                .orElseGet(() ->  lithium.service.cashier.mock.paynl.data.entities.Customer.builder()
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .ipAddress(customer.getIpAddress())
                        .birthDate(customer.getBirthDate())
                        .gender(customer.getGender())
                        .phone(customer.getPhone())
                        .email(customer.getEmail())
                        .language(customer.getLanguage())
                        .trust(Integer.parseInt(Optional.ofNullable(customer.getTrust()).orElse("0")))
                        .reference(customer.getReference())
                        .build());
        
        Payment payment = request.getPayment();
        IBan iBan = request.getPayment().getIBan();
        lithium.service.cashier.mock.paynl.data.entities.IBan iBanEntity = iBanRepository.findByNumber(request.getPayment().getIBan().getNumber()).orElseGet(() -> lithium.service.cashier.mock.paynl.data.entities.IBan.builder()
                .number(iBan.getNumber())
                .bic(iBan.getBic())
                .holder(iBan.getHolder())
                .build());
        
        lithium.service.cashier.mock.paynl.data.entities.Payment paymentEntity = paymentRepository.findByMethod(payment.getMethod()).orElseGet(() -> lithium.service.cashier.mock.paynl.data.entities.Payment.builder()
                .method(payment.getMethod())
                .iBan(iBanEntity)
                .build());
        customerEntity.setPayment(paymentEntity);
        
        Transaction transaction = request.getTransaction();
        lithium.service.cashier.mock.paynl.data.entities.Amount amountEntity = lithium.service.cashier.mock.paynl.data.entities.Amount.builder()
                .currency(transaction.getAmount().getCurrency())
                .value(new BigDecimal(transaction.getAmount().getValue()).longValue())
                .build();

        lithium.service.cashier.mock.paynl.data.entities.Order orderEntity = lithium.service.cashier.mock.paynl.data.entities.Order.builder().orderId(RandomStringUtils.random(10, false, true) + RandomStringUtils.random(6, true, true)).build();

        lithium.service.cashier.mock.paynl.data.entities.Transaction createdTransactionEntity = lithium.service.cashier.mock.paynl.data.entities.Transaction.builder()
                .id("EX-" + RandomStringUtils.random(4, false, true) + "-" +  RandomStringUtils.random(4, false, true) + "-" + RandomStringUtils.random(4, false, true))
                .order(orderEntity)
                .serviceId(transaction.getServiceId())
                .exchangeUrl(transaction.getExchangeUrl())
                .type(transaction.getType())
                .description(request.getTransaction().getDescription())
                .reference(request.getTransaction().getReference())
                .amount(amountEntity)
                .customer(customerEntity)
                .created(String.valueOf(new Date().getTime()))
                .modified(String.valueOf(new Date().getTime() + 1))
                .refundId("RF-" + RandomStringUtils.random(4, false, true) + "-" +  RandomStringUtils.random(4, false, true) + "-" + RandomStringUtils.random(4, false, true))
                .build();
        
        createdTransactionEntity.setOrder(orderEntity);
        
        Stats stats = request.getStats();
        if (stats != null) {
            lithium.service.cashier.mock.paynl.data.entities.Stats statsEntity = lithium.service.cashier.mock.paynl.data.entities.Stats.builder()
                    .info(stats.getInfo())
                    .tool(stats.getTool())
                    .extra1(stats.getExtra1())
                    .extra2(stats.getExtra2())
                    .extra3(stats.getExtra3())
                    .domainId(stats.getDomainId())
                    .build();
            createdTransactionEntity.setStats(statsEntity);
            statsRepository.save(statsEntity);
        }
        
        iBanRepository.save(iBanEntity);
        paymentRepository.save(paymentEntity);
        customerRepository.save(customerEntity);
        amountRepository.save(amountEntity);
        orderRepository.save(orderEntity);
        transactionRepository.save(createdTransactionEntity);
        
        Transaction createdTransaction = mapTransaction(createdTransactionEntity);

        PayoutResponse payoutResponse = getPayoutResponse(createdTransaction);

        Scenario scenario = Scenario.getByAmount(new BigDecimal(transaction.getAmount().getValue()).longValue());
        switch (scenario) {
            //TODO: Simulate pay.nl connection error, implement handling
//            case CONNECTION_ERROR:
//                Thread.sleep(80000);
//                simulatePayout(createdTransaction);
//                return payoutResponse;
            case EMPTY_BODY:
                return PayoutResponse.builder().build();
            default:
                simulatePayout(createdTransaction);
                return payoutResponse;
        }
    }

    public void simulatePayout(Transaction transaction) {
        Scenario scenario = Scenario.getByAmount(new BigDecimal(transaction.getAmount().getValue()).longValue());
        Long delay = Long.parseLong(delayBetweenTransactionSteps);
        switch (scenario)
        {
            case SUCCESS:
                scheduleTransactionStep(transaction, TransactionStatus.PAID.getCode(), delay, WebhookAction.NEW_PPT.getCall(), true);
                break;
            case EXPIRED:
                scheduleTransactionStep(transaction, TransactionStatus.EXPIRED.getCode(), delay, WebhookAction.PAYOUT_REJECTED.getCall(), true);
                break;
            case CANCELED:
                scheduleTransactionStep(transaction, TransactionStatus.CANCEL.getCode(), delay, WebhookAction.PAYOUT_REJECTED.getCall(), true);
                break;
            case DENIED:
                scheduleTransactionStep(transaction, TransactionStatus.DENIED.getCode(), delay, WebhookAction.PAYOUT_REJECTED.getCall(), true);
                break;
            case DENIED_V2:
                scheduleTransactionStep(transaction, TransactionStatus.DENIED_V2.getCode(), delay, WebhookAction.PAYOUT_REJECTED.getCall(), true);
                break;
            case FAILURE:
                scheduleTransactionStep(transaction, TransactionStatus.FAILURE.getCode(), delay, WebhookAction.PAYOUT_FAILED.getCall(), true);
                break;
            case NO_NOTIFICATION:
                scheduleTransactionStep(transaction, TransactionStatus.PAID.getCode(), delay, WebhookAction.NEW_PPT.getCall(), false);
                break;
            case COMPLETED_NO_DELAY:
                scheduleTransactionStep(transaction, TransactionStatus.PAID.getCode(), 0L, WebhookAction.NEW_PPT.getCall(), true);
                break;
            case INCORRECT_FINAL_AMOUNT:
                lithium.service.cashier.mock.paynl.data.entities.Transaction transactionEntity = transactionRepository.findTransactionById(transaction.getId());
                transactionEntity.getAmount().setValue(transactionEntity.getAmount().getValue() + 1L);
                transactionRepository.save(transactionEntity);
                scheduleTransactionStep(mapTransaction(transactionEntity), TransactionStatus.PAID.getCode(), delay, WebhookAction.NEW_PPT.getCall(), true);
                break;
        }
    }
    
    private void simulateTransactionStep(Transaction transaction, String newStatusCode, String webhookStatus, boolean showNotification) throws Exception {
        log.info("Changing transaction " + transaction.getId() + " status to: " + newStatusCode);
        lithium.service.cashier.mock.paynl.data.entities.Transaction transactionEntity = transactionRepository.findTransactionById(transaction.getId());
        transactionEntity.setStatus(newStatusCode);
        transaction.setStatus(TransactionStatus.getTransactionStatusByCode(newStatusCode));
        transactionRepository.save(transactionEntity);
        if (showNotification){
            WebhookData webhookData = buildWebhookData(transaction, webhookStatus);
            notificationService.callWebhook(transaction.getExchangeUrl(), webhookData);
        }
    }
    
    private void scheduleTransactionStep(Transaction transaction, String transactionStatusCode, Long delay, String webhookStatus, boolean sendNotification) {
        taskScheduler.schedule(() ->
        {
            try {
                simulateTransactionStep(transaction, transactionStatusCode, webhookStatus, sendNotification);
            } catch (Exception e) {
                log.error(e.toString());
            }
        }, new Date(System.currentTimeMillis() + delay));
    }

    private PayoutResponse getPayoutResponse(Transaction createdTransaction) {
        return PayoutResponse.builder()
                .transaction(Transaction.builder()
                        .id(createdTransaction.getId())
                        .orderId(createdTransaction.getOrderId())
                        .serviceId(createdTransaction.getServiceId())
                        .description(createdTransaction.getDescription())
                        .reference(createdTransaction.getReference())
                        .amount(createdTransaction.getAmount())
                        .created(createdTransaction.getCreated())
                        .modified(createdTransaction.getModified())
                        .build())
                .build();
    }
    
    private WebhookData buildWebhookData(Transaction transaction, String webhookAction) {
        return WebhookData.builder()
                .action(WebhookAction.getWebhookAction(webhookAction).getCall())
                .orderId(transaction.getOrderId())
                .amount(transaction.getAmount().getValue())
                .refundId(transaction.getRefundId())
                .build();
    }
    
    public PayoutStatusResponse createPayoutResponse(lithium.service.cashier.mock.paynl.data.entities.Transaction transaction) {
        TransactionStatus statusByAction = TransactionStatus.getTransactionStatusByCode(transaction.getStatus());
        Status status = Status.builder()
                .code(String.valueOf(statusByAction.getCode()))
                .action(statusByAction.getAction())
                .build();
        return PayoutStatusResponse.builder()
                .id(transaction.getId())
                .serviceId(transaction.getServiceId())
                .description(transaction.getDescription())
                .reference(transaction.getReference())
                .orderId(transaction.getOrder().getOrderId())
                .ipAddress(transaction.getCustomer().getIpAddress())
                .exchangeUrl(transaction.getExchangeUrl())
                .amount(mapAmount(transaction.getAmount()))
                .created(transaction.getCreated())
                .modified(transaction.getModified())
                .status(status)
                .customer(mapCustomer(transaction.getCustomer()))
                .stats(transaction.getStats()!= null ? mapStats(transaction.getStats()) : null)
                .order(mapOrder(transaction.getOrder()))
                .paymentMethod(PaymentMethod.builder().id("2871").subId("0").name("Payout SEPA Instant").build())
                .integration(Integration.builder().testMode("false").build())
                .transfersData(Collections.emptyList())
                .links(fillLinks(transaction.getId(), transaction.getRefundId()))
                .build();
    }

    private Transaction mapTransaction(lithium.service.cashier.mock.paynl.data.entities.Transaction entity) {
        return Transaction.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getOrderId())
                .type(entity.getType())
                .serviceId(entity.getServiceId())
                .description(entity.getDescription())
                .reference(entity.getReference())
                .exchangeUrl(entity.getExchangeUrl())
                .amount(mapAmount(entity.getAmount()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .status(entity.getStatus() != null ? TransactionStatus.getTransactionStatusByCode(entity.getStatus()) : null)
                .refundId(entity.getRefundId())
                .build();
    }
    
    private Order mapOrder(lithium.service.cashier.mock.paynl.data.entities.Order order) {
        return Order.builder()
                .id(order.getOrderId())
                .countryCode(order.getCountryCode())
                .deliveryDate(order.getDeliveryDate())
                .invoiceDate(order.getInvoiceDate())
                .build();
    }

    private Customer mapCustomer(lithium.service.cashier.mock.paynl.data.entities.Customer customer) {
        return Customer.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .ipAddress(customer.getIpAddress())
                .birthDate(customer.getBirthDate())
                .gender(customer.getGender())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .language(customer.getLanguage())
                .trust(String.valueOf(customer.getTrust()))
                .reference(customer.getReference())
                .bankAccount(mapBankAccount(customer.getPayment().getIBan()))
                .build();
    }
    
    private BankAccount mapBankAccount(lithium.service.cashier.mock.paynl.data.entities.IBan iBan) {
        return BankAccount.builder()
                .iban(iBan.getNumber())
                .bic(iBan.getBic())
                .owner(iBan.getHolder())
                .build();
    }

    private Amount mapAmount(lithium.service.cashier.mock.paynl.data.entities.Amount entity) {
        return Amount.builder()
                .currency(entity.getCurrency())
                .value(new BigDecimal(entity.getValue()).toString())
                .build();
    }

    private Stats mapStats(lithium.service.cashier.mock.paynl.data.entities.Stats entity) {
        return Stats.builder()
                .info(entity.getInfo())
                .tool(entity.getTool())
                .extra1(entity.getExtra1())
                .extra2(entity.getExtra2())
                .extra3(entity.getExtra3())
                .domainId(entity.getDomainId())
                .build();
    }
    
    private List<Links> fillLinks(String transactionId, String refundId) {
        return Arrays.asList(
                Links.builder().url("/transactions/" + transactionId).rel("self").type("GET").build(),
                Links.builder().url("/transactions/" + transactionId + "/capture").rel("capture").type("PATCH").build(),
                Links.builder().url("/transactions/" + transactionId + "/recurring").rel("recurring").type("PATCH").build(),
                Links.builder().url("/transactions/" + transactionId + "/void").rel("void").type("PATCH").build(),
                Links.builder().url("/transactions/" + transactionId + "/approve").rel("approve").type("PATCH").build(),
                Links.builder().url("/transactions/" + transactionId + "/decline").rel("decline").type("PATCH").build(),
                Links.builder().url("/refund/" + refundId).rel("refund").type("GET").build()
        );
    }
    
}
