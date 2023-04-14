package lithium.service.cashier.verifiers;

import lithium.service.access.client.AccessService;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.services.UserService;
import lithium.service.user.client.objects.PlayerBasic;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class BankAccountNameExternalSphonicVerifier implements ProcessorAccountVerifier {
    private final UserService userService;
    private final AccessService accessService;
    private final BankAccountNameInternalVerifier bankAccountNameInternalVerifier;
    private static final String BANK_ACCOUNT_NAME_EXTERNAL_ACCESS_RULE = "ibanAccessRule";
    
    @Override
    public boolean verify(User cashierUser, ProcessorAccount processorAccount) throws Exception {
        lithium.service.user.client.objects.User user = userService.retrieveUserFromUserService(cashierUser);

        //do internal bank name comparing first comparing first
        if (bankAccountNameInternalVerifier.verify(cashierUser, processorAccount)) {
            return true;
        }

        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("iban", processorAccount.getData().get("iban"));
        additionalData.put("guid", cashierUser.getGuid());
        PlayerBasic playerBasic = PlayerBasic.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .additionalData(additionalData)
                .build();
        try {
            AuthorizationResult authorizationResult = accessService.checkAuthorization(user.getDomain().getName(), BANK_ACCOUNT_NAME_EXTERNAL_ACCESS_RULE, null, Collections.emptyMap(), null, null, false, playerBasic, playerBasic.getAdditionalData());
            return Optional.ofNullable(authorizationResult).map(AuthorizationResult::isSuccessful).orElse(true);
        } catch (Status551ServiceAccessClientException e) {
            log.error("Failed to verify " + ProcessorAccountVerificationType.BANK_ACCOUNT_NAME_EXTERNAL_SPHONIC.getName() + "Access service exception: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public ProcessorAccountVerificationType getType() {
        return ProcessorAccountVerificationType.BANK_ACCOUNT_NAME_EXTERNAL_SPHONIC;
    }
}
