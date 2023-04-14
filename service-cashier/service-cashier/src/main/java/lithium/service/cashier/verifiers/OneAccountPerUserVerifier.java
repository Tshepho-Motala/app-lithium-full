package lithium.service.cashier.verifiers;

import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.ProcessorUserCardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static lithium.service.cashier.client.objects.PaymentMethodStatusType.isActiveAccountStatus;

@Slf4j
@Component
@AllArgsConstructor
public class OneAccountPerUserVerifier implements ProcessorAccountVerifier {
    
    private final ProcessorUserCardRepository processorUserCardRepository;

    @Override
    public boolean verify(User cashierUser, ProcessorAccount processorAccount) throws Exception {
        String guid = cashierUser.getGuid();
        if (processorAccount == null || guid == null) {
            log.warn("Can't validate processor account owner. Missing arguments: " + "processorAccount: " + processorAccount + ", userGuid: " + guid);
            throw new Exception("Missing arguments");
        }
        List<ProcessorUserCard> processorUserCards = processorUserCardRepository.findByUserGuidAndTypeName(guid, processorAccount.getType().getName());

        return !processorUserCards.stream().anyMatch(uc -> isActiveAccountStatus(PaymentMethodStatusType.fromName(uc.getStatus().getName()), null) && !uc.getReference().equals(processorAccount.getReference()));
    }

    @Override
    public ProcessorAccountVerificationType getType() {
        return ProcessorAccountVerificationType.ONE_ACCOUNT_PER_USER;
    }
}
