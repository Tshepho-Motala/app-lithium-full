package lithium.service.cashier.verifiers;

import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.services.UserService;
import lithium.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Slf4j
@Component
@AllArgsConstructor
public class BankAccountNameInternalVerifier implements ProcessorAccountVerifier {
    
    private final UserService userService;

    private static final Pattern BANK_NAMES_PATTERN = Pattern.compile("[^a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð'-]+");
    
    @Override
    public boolean verify(User cashierUser, ProcessorAccount processorAccount) throws Exception {
        lithium.service.user.client.objects.User user = userService.retrieveUserFromUserService(cashierUser);
        return isUsernameMatchedWithBankName(user.getFirstName(), user.getLastName(), processorAccount.getName());
    }

    @Override
    public ProcessorAccountVerificationType getType() {
        return ProcessorAccountVerificationType.BANK_ACCOUNT_NAME_INTERNAL;
    }

    private boolean isUsernameMatchedWithBankName(String userFirstName, String userLastName, String bankName) {
        if (StringUtil.isEmpty(bankName)) {
            log.info("Comparing bank names (firstName: " + userFirstName + ", lastName:" + userLastName + ", bankName: " + bankName + ", result: false, bank name is empty" );
            return false;
        }
        String[] bankNames = BANK_NAMES_PATTERN.split(bankName);

        boolean isFullNameMatched = isLastNameMatched(userLastName, bankNames) && isFirstNameInitialMatched(userFirstName, userLastName, bankNames);
        log.info("Comparing bank names (firstName: " + userFirstName + ", lastName: " + userLastName + ", bankName: " + bankName + ", result: " + isFullNameMatched);
        return isFullNameMatched;
    }

    private static boolean isLastNameMatched(String userLastName, String[] bankNames) {
        return Arrays.stream(bankNames)
                .anyMatch(userLastName::equalsIgnoreCase);
    }
    private static boolean isFirstNameInitialMatched(String userFirstName, String userLastName, String[] bankNames) {
        return Arrays.stream(bankNames)
                .filter(Predicate.not(userLastName::equalsIgnoreCase))
                .anyMatch(n -> n.substring(0, 1).equalsIgnoreCase(userFirstName.substring(0, 1)));
    }
    
}
