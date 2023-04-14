package lithium.service.cashier.services;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.frontend.DoRequest;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.machine.DoMachineContext;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.util.DomainToPlaceholderBinder;
import lithium.service.user.client.utils.UserToPlaceholderBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static java.util.Optional.ofNullable;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_PROCESSOR_RESPONSE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_REQUEST;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_RESPONSE;


@Service
@Slf4j
public class CashierCommunicationService {
    @Autowired
    LithiumServiceClientFactory services;
    @Autowired
    TransactionService transactionService;
    @Autowired
    CashierService cashierService;
    @Autowired
    CashierPlaceholderService placeholderService;

    public static final String RECIPIENT_INTERNAL = "internal";
    public static final String RECIPIENT_PLAYER = "player";
    public static final String RECIPIENT_EXTERNAL = "external";

    protected static final String ISO_LANG_CODE_ENG = "en";

    protected String cashierTransactionStatusNotification(String transactionType, String transactionStatus, String recipient) {
        //[transactionType].[status].[recipient]
        //service-cashier/service-cashier/src/main/resources/emailtemplates/depositwithdrawal.json
        return transactionType.toLowerCase() + "." + transactionStatus.toLowerCase() + "." + recipient.toLowerCase();
    }

    protected Set<Placeholder> constructPlaceholders(DoMachineContext context) {
        Set<Placeholder> placeholders = new HashSet<>(
                new UserToPlaceholderBinder(context.getExternalUser())
                        .completePlaceholders());

        placeholders.addAll(new DomainToPlaceholderBinder(context.getExternalDomain()).completePlaceholders());

        placeholders.addAll(placeholderService.buildForTransaction(context.getTransaction()));

        placeholders.add(CASHIER_PROCESSOR_RESPONSE.from(ofNullable(context.getProcessorResponse()).map(DoProcessorResponse::toString)));
        placeholders.add(CASHIER_REQUEST.from(ofNullable(context.getRequest()).map(DoRequest::toString)));
        placeholders.add(CASHIER_RESPONSE.from(ofNullable(context.getResponse()).map(DoResponse::toString)));
        return placeholders;
    }

    protected boolean isStatusValid(String status) {
        DoMachineState[] states = DoMachineState.values();
        boolean validState = false;
        for (int i = 0; i < states.length && !validState; i++) {
            if (states[i].name().toLowerCase().equals(status.toLowerCase())) {
                validState = true;
            }
        }
        return validState;
    }
}
