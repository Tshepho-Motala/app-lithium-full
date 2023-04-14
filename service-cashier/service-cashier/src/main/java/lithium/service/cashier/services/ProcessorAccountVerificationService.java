package lithium.service.cashier.services;

import lithium.service.cashier.client.internal.VerifyProcessorAccountRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountResponse;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.verifiers.ProcessorAccountVerifier;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static lithium.service.cashier.client.objects.PaymentMethodStatusType.isActiveOrHistoricAccountStatus;

@Slf4j
@Service
public class ProcessorAccountVerificationService {
    private final UserService userService;
    private final ProcessorAccountService processorAccountService;
    private final DomainMethodProcessorService dmpService;
    private final Map<ProcessorAccountVerificationType, ProcessorAccountVerifier> processorAccountVerifiersMap;
    
    public ProcessorAccountVerificationService(UserService userService, ProcessorAccountService processorAccountService, DomainMethodProcessorService dmpService, List<ProcessorAccountVerifier> processorAccountVerifiersList) {
        this.userService = userService;
        this.processorAccountService = processorAccountService;
        this.dmpService = dmpService;
        processorAccountVerifiersMap = processorAccountVerifiersList.stream()
                .collect(Collectors.toMap(ProcessorAccountVerifier::getType, Function.identity()));
    }
    
    public boolean checkProcessorAccountsCount(User user, DomainMethodProcessor domainMethodProcessor) {
        return getMaxActiveAccountsCount(domainMethodProcessor).map(c ->  processorAccountService.getActiveProcessorAccountsPerProcessor(user, domainMethodProcessor) < c).orElse(true);
    }
    
    public VerifyProcessorAccountResponse verify(VerifyProcessorAccountRequest verificationRequest) throws Exception {
        ProcessorAccount processorAccount = verificationRequest.getProcessorAccount();
        User cashierUser = userService.findOrCreate(verificationRequest.getUserGuid());

        processorAccount = verifyProcessorAccount(cashierUser, processorAccount, verificationRequest.getVerifications(), verificationRequest.isUpdate());

        return VerifyProcessorAccountResponse.builder().result(processorAccount.getVerified()).processorAccount(processorAccount).build();
    }

    public ProcessorAccount verifyProcessorAccount(User cashierUser, ProcessorAccount processorAccount, DomainMethodProcessor domainMethodProcessor, boolean update) throws Exception {
        return verifyProcessorAccount(cashierUser, processorAccount, getAccountVerifications(domainMethodProcessor), update);
    }

    private ProcessorAccount verifyProcessorAccount(User cashierUser, ProcessorAccount processorAccount, List<ProcessorAccountVerificationType> verifications, boolean update) throws Exception {
        ProcessorAccount storedProcessorAccount = processorAccountService.getProcessorAccount(cashierUser, processorAccount.getReference(), processorAccount.getData() != null ? processorAccount.getData().get("fingerprint") : null);

        if (storedProcessorAccount != null) {
            if (!isActiveOrHistoricAccountStatus(storedProcessorAccount.getStatus(), null)) {
                processorAccount.setId(storedProcessorAccount.getId());
                processorAccount.setVerified(false);
                processorAccount.setStatus(storedProcessorAccount.getStatus());
                processorAccount.setFailedVerification(ProcessorAccountVerificationType.ACTIVE_ACCOUNT);
            } else if (!update) {
                processorAccount = storedProcessorAccount;
            }
        }
        if (processorAccount.getVerified() == null && !verifications.isEmpty()) {
            VerificationResult verificationResult = getVerificationResult(cashierUser, processorAccount, verifications);
            processorAccount.setFailedVerification(verificationResult.failedVerificationType());
            processorAccount.setVerified(verificationResult.isVerified());
        }
       
        return processorAccount;
    }

    private VerificationResult getVerificationResult(User cashierUser, ProcessorAccount processorAccount, List<ProcessorAccountVerificationType> verifications) {
        return verifications.stream()
                .filter(Objects::nonNull)
                .filter(verificationType -> verificationType.isApplicable(processorAccount.getType()))
                .filter(Predicate.not(isVerified(cashierUser, processorAccount)))
                .map(VerificationResult::toVerificationFailed)
                .findFirst()
                .orElseGet(VerificationResult::toVerificationSuccess);
    }

    private Predicate<ProcessorAccountVerificationType> isVerified(User cashierUser, ProcessorAccount processorAccount) {
        return verificationType -> getProcessorAccountVerifier(verificationType)
                .map(accountVerifier -> returnExceptionAsFalse(() -> accountVerifier.verify(cashierUser, processorAccount), verificationType))
                .orElseGet(() -> {
                        log.error("Unknown processor account verification type: " + verificationType + "Processor account: " + processorAccount);
                        return false;
        });
    }
    
    private record VerificationResult(boolean isVerified, ProcessorAccountVerificationType failedVerificationType){
        public static VerificationResult toVerificationFailed(ProcessorAccountVerificationType failedVerificationType){
            return new VerificationResult(false, failedVerificationType);
        }
        public static VerificationResult toVerificationSuccess(){
            return new VerificationResult(true, null);
        }
    }    


    private boolean returnExceptionAsFalse(Callable<Boolean> verifier, ProcessorAccountVerificationType verificationType) {
        try {
            return verifier.call();
        } catch (Exception e) {
            log.error("Processor account is invalid. Verification: " + verificationType);
            return false;
        }
    }

    //verification list is taken from the processor properties
    private List<ProcessorAccountVerificationType> getAccountVerifications(DomainMethodProcessor domainMethodProcessor) {
        String accountVerifications = dmpService.getPropertyValue(domainMethodProcessor, "account_verifications");
        if (StringUtil.isEmpty(accountVerifications)) {
            return Collections.emptyList();
        }
        return Arrays.asList(accountVerifications.split("\\s*,\\s*")).stream().map(ProcessorAccountVerificationType::fromName).collect(Collectors.toList());
    }

    private Optional<Integer> getMaxActiveAccountsCount(DomainMethodProcessor domainMethodProcessor) {
        try {
            return Optional.ofNullable(dmpService.getPropertyValue(domainMethodProcessor, "max_active_accounts")).filter(p -> !StringUtil.isEmpty(p)).map(Integer::parseInt);
        } catch(Exception e) {
            log.error("The max_active_accounts " + domainMethodProcessor.getDescription() + " domain method processor property is invalid. Exception: " + e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    private Optional<ProcessorAccountVerifier> getProcessorAccountVerifier(ProcessorAccountVerificationType verificationType) {
        return ofNullable(processorAccountVerifiersMap.get(verificationType));
    }
    
}
