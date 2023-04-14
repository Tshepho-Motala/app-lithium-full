package lithium.service.cashier.services;

import lithium.math.CurrencyAmount;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.Method;
import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.DomainMethodProcessorRepository;
import lithium.service.cashier.data.repositories.DomainMethodRepository;
import lithium.service.cashier.data.repositories.MethodRepository;
import lithium.service.cashier.data.repositories.ProcessorRepository;
import lithium.service.cashier.data.repositories.TransactionRepository;
import lithium.service.cashier.data.repositories.TransactionWorkflowHistoryRepository;
import lithium.service.libraryvbmigration.data.dto.HistoricCashierTransaction;
import lithium.service.libraryvbmigration.data.dto.LegacyCashierPaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class HistoricTransactionsOperatorMigrationService {
    private final DomainService domainService;
    private final DomainMethodRepository domainMethodRepository;
    private final DomainMethodProcessorRepository domainMethodProcessorRepository;
    private final MethodRepository methodRepository;
    private final ProcessorRepository processorRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final TransactionWorkflowHistoryRepository transactionWorkflowHistoryRepository;

    @Autowired
    public HistoricTransactionsOperatorMigrationService(DomainService domainService,
            DomainMethodRepository domainMethodRepository, DomainMethodProcessorRepository domainMethodProcessorRepository,
            MethodRepository methodRepository, ProcessorRepository processorRepository,
            TransactionRepository transactionRepository, TransactionService transactionService,
            TransactionWorkflowHistoryRepository transactionWorkflowHistoryRepository) {
        this.domainService = domainService;
        this.domainMethodRepository = domainMethodRepository;
        this.domainMethodProcessorRepository = domainMethodProcessorRepository;
        this.methodRepository = methodRepository;
        this.processorRepository = processorRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
        this.transactionWorkflowHistoryRepository = transactionWorkflowHistoryRepository;
    }

    @Transactional
    public DomainMethodProcessor createLegacyDomainMethodProcessor(
        LegacyCashierPaymentMethod paymentMethod) throws Exception {

            Domain domain = domainService.findByName(paymentMethod.getDomainName());
            String paymentMethodCode = getCodeFromName(paymentMethod.getPaymentMethodName());
            String paymentProviderCode = getCodeFromName(paymentMethod.getPaymentProviderName());
            Method method = findOrCreateMethod(paymentMethod.getPaymentMethodName(),
                paymentMethodCode);
            DomainMethod domainMethod = findOrCreateDomainMethod(method, domain);
            Processor processor = findOrCreateProcessor(paymentProviderCode,
                paymentMethod.getPaymentProviderName());
            DomainMethodProcessor domainMethodProcessor = domainMethodProcessorRepository
                .findByDomainMethodIdAndProcessorId(domainMethod.getId(), processor.getId());
            if (domainMethodProcessor == null) {
                domainMethodProcessor = domainMethodProcessorRepository.save(
                    DomainMethodProcessor.builder()
                        .deleted(true)
                        .description("Legacy - " + paymentMethod.getPaymentMethodName() + " - "
                            + paymentMethod.getPaymentProviderName())
                        .enabled(false)
                        .active(false)
                        .domainMethod(domainMethod)
                        .processor(processor)
                        .build()
                );
            }
            return domainMethodProcessor;

    }

    @Transactional(rollbackFor = Exception.class)
    public Transaction createHistoricCashierTransaction(HistoricCashierTransaction historicCashierTransaction,
                                                        User user, TransactionStatus transactionStatus) {
        String paymentMethodCode = getCodeFromName(historicCashierTransaction.getPaymentMethod());
        String paymentProviderCode = getCodeFromName(historicCashierTransaction.getPaymentProvider());
        String domainName = user.domainName();

        DomainMethod domainMethod = domainMethodRepository.findByMethodCodeAndDomainName(paymentMethodCode, domainName);
        Processor processor = processorRepository.findByCode(paymentProviderCode);
        DomainMethodProcessor domainMethodProcessor = domainMethodProcessorRepository
                .findByDomainMethodIdAndProcessorId(domainMethod.getId(), processor.getId());

        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .amountCents(CurrencyAmount.fromAmount(historicCashierTransaction.getAmount()).toCents())
                        .createdOn(historicCashierTransaction.getCreatedDate())
                        .currencyCode(historicCashierTransaction.getCurrencyCode())
                        .processorReference(String.valueOf(historicCashierTransaction.getTransactionId()))
                        .transactionType(TransactionType.fromDescription(historicCashierTransaction.getType()))
                        .domainMethod(domainMethod)
                        .user(user)
                        .directWithdrawal(false)
                        .forcedSuccess(false)
                        .manual(false)
                        .retryProcessing(false)
                        .status(transactionStatus)
                        .build()
        );

        transactionService.setData(transaction, "amount", String.valueOf(historicCashierTransaction.getAmount()),
                1, false);

        TransactionWorkflowHistory transactionWorkflowHistory = transactionWorkflowHistoryRepository.save(
                TransactionWorkflowHistory.builder()
                        .stage(1)
                        .timestamp(historicCashierTransaction.getCreatedDate())
                        .processor(domainMethodProcessor)
                        .status(transactionStatus)
                        .transaction(transaction)
                        .build()
        );

        transaction.setCurrent(transactionWorkflowHistory);

        return transaction;
    }

    private Processor findOrCreateProcessor(String paymentProviderCode, String paymentProviderName) {
        Processor processor = processorRepository.findByCode(paymentProviderCode);
        if (processor == null) {
            processor = processorRepository.save(
                    Processor.builder()
                            .code(paymentProviderCode)
                            .name("Legacy - " + paymentProviderName)
                            .enabled(false)
                            .deposit(true)
                            .withdraw(true)
                            .url(paymentProviderCode)
                            .build()
            );
        }
        return processor;
    }

    private DomainMethod findOrCreateDomainMethod(Method method, Domain domain) {
        DomainMethod domainMethod = domainMethodRepository.findByMethodCodeAndDomainName(method.getCode(),
                domain.getName());
        if (domainMethod == null) {
            domainMethod = domainMethodRepository.save(
                    DomainMethod.builder()
                            .name("Legacy - " + method.getName())
                            .description("Legacy - " + method.getName())
                            .method(method)
                            .domain(domain)
                            .enabled(false)
                            .deleted(true)
                            .deposit(true)
                            .build()
            );
        }
        return domainMethod;
    }

    private Method findOrCreateMethod(String paymentMethodName, String paymentMethodCode) {
        Method method = methodRepository.findByCode(paymentMethodCode);
        if (method == null) {
            method = methodRepository.save(
                    Method.builder()
                            .code(paymentMethodCode)
                            .enabled(false)
                            .name(paymentMethodName)
                            .build());
        }
        return method;
    }

    private String getCodeFromName(String name) {
        return "legacy-" + name.replaceAll(" ", "-");
    }
}
