package lithium.service.cashier.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.client.CashierProcessorUserCardRetrievalClient;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.config.ServiceCashierConfigurationProperties;
import lithium.service.cashier.config.upo.migration.DomainMethodProcessorMapping;
import lithium.service.cashier.config.upo.migration.ProcessorAccountTypeMapping;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.DomainMethodProcessorRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProcessorUserCardMigrationService {
    private final DomainMethodProcessorRepository domainMethodProcessorRepository;
    private final LithiumServiceClientFactory services;
    private final ProcessorAccountService processorAccountService;
    private final ServiceCashierConfigurationProperties properties;

    @Autowired
    public ProcessorUserCardMigrationService(DomainMethodProcessorRepository domainMethodProcessorRepository,
            LithiumServiceClientFactory services, ProcessorAccountService processorAccountService,
            ServiceCashierConfigurationProperties properties) {
        this.domainMethodProcessorRepository = domainMethodProcessorRepository;
        this.services = services;
        this.processorAccountService = processorAccountService;
        this.properties = properties;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ProcessorUserCard> migrateProcessorUserCards(String domainName, User user, String userTokenId,
            String methodCode, String processorCode) throws Status500InternalServerErrorException {
        DomainMethodProcessor lookupFromDomainMethodProcessor = findDomainMethodProcessor(domainName, methodCode,
                processorCode);

        CashierProcessorUserCardRetrievalClient client = getClient(lookupFromDomainMethodProcessor.getProcessor()
                .getUrl());

        List<ProcessorAccount> userCards = client.retrieveUserPaymentOptions(domainName, userTokenId);

        List<ProcessorUserCard> processorUserCards = new ArrayList<>();
        userCards.stream().forEach(userCard -> {
            try {
                DomainMethodProcessor assignToDomainMethodProcessor = getDomainMethodProcessorByProcessorAccountType(
                        domainName, userCard.getType());

                processorUserCards.add(
                        processorAccountService.saveProcessorAccount(user, assignToDomainMethodProcessor, userCard,
                                true));
            } catch (Exception e) {
                log.error("Failed to save processor account | {} | user: {}, userTokenId: {} | {}", userCard, user,
                        userTokenId, e.getMessage(), e);
                // Continue with others
                // Should fail and push back to queue for DLQ retry and eventual PL addition instead?
            }
        });

        return processorUserCards;
    }

    private DomainMethodProcessor getDomainMethodProcessorByProcessorAccountType(String domainName,
            ProcessorAccountType type) throws Status500InternalServerErrorException {
        DomainMethodProcessorMapping mapping = switch (type) {
            case CARD -> properties.getUserPaymentOptionsMigration().getProcessorAccountTypeMapping().getCard();
            case PAYPAL -> properties.getUserPaymentOptionsMigration().getProcessorAccountTypeMapping().getPaypal();
            case HISTORIC -> properties.getUserPaymentOptionsMigration().getProcessorAccountTypeMapping().getHistoric();
            default -> throw new Status500InternalServerErrorException("Unsupported processor account type");
        };

        String methodCode = mapping.getMethodCode();
        String processorCode = mapping.getProcessorCode();

        return findDomainMethodProcessor(domainName, methodCode, processorCode);
    }

    private DomainMethodProcessor findDomainMethodProcessor(String domainName, String methodCode, String processorCode)
            throws Status500InternalServerErrorException {
        DomainMethodProcessor domainMethodProcessor = domainMethodProcessorRepository
                .findFirstByEnabledTrueAndDomainMethodDomainNameAndDomainMethodMethodCodeAndProcessorCode(domainName,
                        methodCode, processorCode);
        if (domainMethodProcessor == null) {
            throw new Status500InternalServerErrorException("Unable to find domain method processor for method "
                    + methodCode + " and processor " + processorCode);
        }
        return domainMethodProcessor;
    }

    private CashierProcessorUserCardRetrievalClient getClient(String processorUrl)
            throws Status500InternalServerErrorException {
        Optional<CashierProcessorUserCardRetrievalClient> client = getClient(
                CashierProcessorUserCardRetrievalClient.class, processorUrl);

        if (client.isEmpty()) {
            throw new Status500InternalServerErrorException("Unable to retrieve client for " + processorUrl);
        }

        return client.get();
    }

    private <E> Optional<E> getClient(Class<E> theClass, String url) {
        E clientInstance = null;

        try {
            clientInstance = services.target(theClass, url, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage(), e);
        }

        return Optional.ofNullable(clientInstance);
    }
}
