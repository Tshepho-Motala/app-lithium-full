package lithium.service.cashier.jobs.migration;

import lithium.cashier.CashierTransactionLabels;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.accounting.objects.TransactionLabelContainer;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.ProcessorAccountStatus;
import lithium.service.cashier.data.entities.ProcessorAccountType;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionData;
import lithium.service.cashier.data.repositories.DomainMethodProcessorRepository;
import lithium.service.cashier.data.repositories.ProcessorUserCardRepository;
import lithium.service.cashier.data.repositories.TransactionDataRepository;
import lithium.service.cashier.data.repositories.TransactionRepository;
import lithium.service.cashier.services.CashierService;
import lithium.service.cashier.services.ProcessorAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@Component
@Slf4j
public class PopulateTransactionsPaymentMethodsJob {

    @Autowired
    private CashierService cashierService;
    @Autowired
    private CashierDoCallbackService cashierDoCallbackService;
    @Autowired
    private AccountingClientService accountingClientService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionDataRepository transactionDataRepository;
    @Autowired
    private ProcessorUserCardRepository processorUserCardRepository;
    @Autowired
    private ProcessorAccountService processorAccountService;
    @Autowired
    private DomainMethodProcessorRepository domainMethodProcessorRepository;

    private static final String SUCCESS_STATUS_LABEL = "SUCCESS";

    private boolean isPopulationJobStarted;
    private boolean forceDisableJob;

    @Bean
    public ExecutorService populationExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Async("populationExecutor")
    public void executePopulationTransactionJob(boolean dryRun, boolean onePagePopulationFlag, int pageSize, Long delay) throws Exception {
        log.info("Start transaction population, dryRun={}, pageSize={}", dryRun, pageSize);
        cashierDoCallbackService.populateTransactionsPaymentMethods(dryRun, onePagePopulationFlag, pageSize, delay);
    }

    public void populateTransactionsPaymentMethods(boolean dryRun, boolean onePagePopulationFlag, int pageSize, Long delay) throws InterruptedException {
        log.info("/populate-transactions-payment-methods-job, isPopulationJobStarted={}", isPopulationJobStarted);
        if (!isPopulationJobStarted) {
            isPopulationJobStarted = true;
            PageRequest pageRequest = PageRequest.of(0, pageSize);
            Page<Transaction> transactions = getTransactions(pageRequest);
            do {
                for (Transaction transaction : transactions) {
                    try {
                        String transactionTypeCode = getTransactionTypeCode(transaction);
                        if (isNull(transactionTypeCode)) {
                            log.warn("Unexpected transaction type (" + transaction.getId() + "): " + transaction.getTransactionType());
                            continue;
                        }
                        Long externalTransactionId = accountingClientService.findExternalTransactionId(String.valueOf(transaction.getId()), transactionTypeCode);
                        if (isNull(externalTransactionId)) {
                            log.error("Can't get external transaction id for " + transaction.getId());
                            continue;
                        }
                        if (!dryRun) {
                            executePopulationTransactionJob(transaction, externalTransactionId);
                        } else {
                            log.debug("Populated transaction and registered account queue ({}): {}", transaction.getId(), transaction);
                        }
                    } catch (Exception e) {
                        log.error("Got error during update historic financial data of transaction (" + transaction.getId() + ")" +
                                "\n:: Disabling job...", e);
                    }
                }
                if (forceDisableJob) break;
                log.info("Transactions added to queue to update historic financial data: " + transactions.getNumberOfElements());
                if (onePagePopulationFlag) break;
                Thread.sleep(delay);
                Pageable newPage = dryRun ? transactions.nextPageable() : pageRequest;
                transactions = getTransactions(newPage);
            } while (isNotAllTransactionsPopulated(dryRun, transactions));
            isPopulationJobStarted = false;
            forceDisableJob = false;
        }
    }

    public void executePopulationTransactionJob(Transaction transaction, Long externalTransactionId) throws Exception {
        populatePaymentMethod(transaction);
        registerTransactionLabel(transaction, externalTransactionId);
        transactionRepository.save(transaction);
    }

    private boolean isNotAllTransactionsPopulated(boolean dryRun, Page<Transaction> transactions) {
        return dryRun ? !transactions.isFirst() && transactions.hasContent() : transactions.hasContent();
    }

    private Page<Transaction> getTransactions(Pageable pageRequest) {
        return transactionRepository.findByStatusCodeAndPaymentMethodNullOrderByUserId(SUCCESS_STATUS_LABEL, pageRequest);
    }

    private String getTransactionTypeCode(Transaction transaction) {
        String transactionTypeCode = null;
        if (TransactionType.DEPOSIT.equals(transaction.getTransactionType())) {
            transactionTypeCode = "CASHIER_DEPOSIT";
        } else if (TransactionType.WITHDRAWAL.equals(transaction.getTransactionType())) {
            transactionTypeCode = "CASHIER_PAYOUT";
        }
        return transactionTypeCode;
    }

    private void populatePaymentMethod(Transaction t) throws Exception {
        TransactionData tData = getByTransactionAndField(t);
        if (tData != null && tData.getValue() != null) {
            ProcessorUserCard paymentMethod = processorUserCardRepository.findByUserAndReference(t.getUser(), tData.getValue());
            if (paymentMethod != null) {
                t.setPaymentMethod(paymentMethod);
            } else {
                findOrCreateHistoricPaymentMethod(t);
            }
        } else {
            findOrCreateHistoricPaymentMethod(t);
        }
    }

    private void findOrCreateHistoricPaymentMethod(Transaction t) throws Exception {
        List<DomainMethodProcessor> dmps = domainMethodProcessorRepository.findByDomainMethodId(t.getDomainMethod().getId());
        if (!dmps.isEmpty()) {
            DomainMethodProcessor domainMethodProcessor = dmps.get(0);
            String historicReference = "historic-" + domainMethodProcessor.getDomainMethod().getMethod().getCode().toLowerCase() + "-" + t.getUser().guid();
            ProcessorUserCard paymentMethod = processorUserCardRepository.findByUserAndReference(t.getUser(), historicReference);
            if (paymentMethod == null) {
                paymentMethod = createHistoricPaymentMethod(t, domainMethodProcessor, historicReference);
            }
            t.setPaymentMethod(paymentMethod);
        } else {
            log.error("Can't find DomainMethodProcessor for transaction: " + t);
            throw new Exception("Can't find DomainMethodProcessor for transaction");
        }
    }

    private ProcessorUserCard createHistoricPaymentMethod(Transaction t, DomainMethodProcessor domainMethodProcessor, String historicReference) {
        ProcessorUserCard paymentMethod = new ProcessorUserCard();
        payloadMainDataToPaymentMethod(paymentMethod, t, domainMethodProcessor, historicReference);
        return processorUserCardRepository.save(paymentMethod);
    }

    private void payloadMainDataToPaymentMethod(ProcessorUserCard paymentMethod, Transaction t, DomainMethodProcessor dmp, String reference) {
        paymentMethod.setUser(t.getUser());
        paymentMethod.setIsDefault(false);
        paymentMethod.setIsActive(false);
        paymentMethod.setHideInDeposit(false);
        paymentMethod.setDomainMethodProcessor(dmp);
        paymentMethod.setReference(reference);
        paymentMethod.setFingerprint("");
        ProcessorAccountStatus processorAccountStatus = processorAccountService.findOrCreateHistoricProcessorAccountStatus();
        paymentMethod.setStatus(processorAccountStatus);
        ProcessorAccountType processorAccountType = processorAccountService.findOrCreateHistoricProcessorAccountType();
        paymentMethod.setType(processorAccountType);
    }

    private void registerTransactionLabel(Transaction t, long accountingTransactionId) {
        TransactionLabelBasic tlb = TransactionLabelBasic.builder()
                .labelName(CashierTransactionLabels.PLAYER_PAYMENT_METHOD_REFERENCE)
                .labelValue(String.valueOf(t.getPaymentMethod().getId()))
                .summarize(true)
                .build();

        List<TransactionLabelBasic> transactionLabelBasicList = new ArrayList<>();
        transactionLabelBasicList.add(tlb);

        TransactionLabelContainer transactionLabelContainer = TransactionLabelContainer.builder()
                .transactionId(accountingTransactionId)
                .labelList(transactionLabelBasicList)
                .build();

        log.debug("Put cashier transaction to register account queue ({}): {}", t.getId(), transactionLabelContainer);
        cashierService.registerTransactionLabelContainer(transactionLabelContainer);
    }

    private TransactionData getByTransactionAndField(Transaction t) {
        return ofNullable(transactionDataRepository.findByTransactionAndFieldAndStageAndOutput(t, "cardReference", 1, false)).
                orElseGet(() -> ofNullable(transactionDataRepository.findByTransactionAndFieldAndStageAndOutput(t, "cardSourceId", 1, false))
                        .orElse(null));
    }

    public String getTransactionsNumberWithoutPaymentMethods() {
        final String SUCCESS_STATUS_LABEL = "SUCCESS";
        List<Transaction> transactions = transactionRepository.findByStatusCodeAndPaymentMethodNull(SUCCESS_STATUS_LABEL);
        String numberOfTransactionsMessage = "Number of transactions without payment methods (unpopulated): " + transactions.size();
        log.info(numberOfTransactionsMessage);
        return numberOfTransactionsMessage;
    }

    public void forceDisableJob() {
        forceDisableJob = true;
    }
}
