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

import static java.util.Optional.ofNullable;
import static lithium.service.cashier.client.objects.PaymentMethodStatusType.isActiveAccountStatus;

@Slf4j
@Component
@AllArgsConstructor
public class DuplicateAccountVerifier implements ProcessorAccountVerifier {
    private final ProcessorUserCardRepository processorUserCardRepository;
    
    @Override
    public boolean verify(User cashierUser, ProcessorAccount processorAccount) throws Exception {
        return verify(cashierUser.getGuid(), ofNullable(processorAccount.getData()).map(d -> d.get("fingerprint")).orElse(null), processorAccount.getReference(), null);
    }

    public boolean verify(String userGuid, String fingerprint, String reference, Boolean isDeposit) throws Exception {
        if (fingerprint == null && reference == null || userGuid == null) {
            log.error("Failed perform duplicate account check. Incorrect input data. User: " + userGuid + " Processor account reference: " + reference + " Fingerprint: " + fingerprint);
            throw new Exception("Failed perform duplicate account check. Incorrect input data.");
        }

        List<ProcessorUserCard> savedProcessorAccounts = (fingerprint != null && !fingerprint.isEmpty())
                ? processorUserCardRepository.findByFingerprint(fingerprint)
                : processorUserCardRepository.findByReference(reference);
        //card owner check is success if there is no card with current fingerprint
        //or all of the card with current fingerprint are belongs to the current user and not diabled o blocked
        //or registered to other user AND disabled
        return savedProcessorAccounts == null || savedProcessorAccounts.stream().allMatch(uc ->  uc == null
                || userGuid.equalsIgnoreCase(uc.getUser().getGuid()) && isActiveAccountStatus(PaymentMethodStatusType.fromName(uc.getStatus().getName()), isDeposit)
                || !userGuid.equalsIgnoreCase(uc.getUser().getGuid()) && PaymentMethodStatusType.fromName(uc.getStatus().getName()) == PaymentMethodStatusType.DISABLED);
    }

    @Override
    public ProcessorAccountVerificationType getType() {
        return ProcessorAccountVerificationType.DUPLICATE_ACCOUNT;
    }
}
