package lithium.service.access.provider.transunion.service;

import lithium.service.access.provider.transunion.shema.response.success.AgeVerify;
import lithium.service.access.provider.transunion.shema.response.success.CallValidateIdentityCheck;
import lithium.service.access.provider.transunion.shema.response.success.SystemData;
import lithium.service.access.provider.transunion.shema.response.success.TransUnionResponse;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.kyc.client.KycResultsClient;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class VerificationResultService {
    public static final String TRANSUNION = "Transunion";
    public static final String CALL_VALIDATE_5 = "CallValidate5";
    public static final String NOT_AVAILABLE = "Not available";
    @Autowired
    LithiumServiceClientFactory services;

    public void sendVerificationResult(TransUnionResponse transUnionResponse, User user, boolean success, String message) throws LithiumServiceClientFactoryException {
        log.debug("Requested attempt to save the result of the transunion kyc verification result for user:" + user.getGuid());
        KycResultsClient kycResultsClient = services.target(KycResultsClient.class, "service-kyc", true);
        VerificationKycAttempt attempt = VerificationKycAttempt.builder()
                .userGuid(user.guid())
                .domainName(user.getDomain().getName())
                .providerName(TRANSUNION)
                .methodName(CALL_VALIDATE_5)
                .build();

        KycSuccessVerificationResponse.KycSuccessVerificationResponseBuilder builder = KycSuccessVerificationResponse.builder();
        builder.resultMessageText(message);
        String providerRequestId = NOT_AVAILABLE;
        Optional<SystemData> systemData = Optional.ofNullable(transUnionResponse.getSearchResultBody().getSystemData());
        if (systemData.isPresent() && !systemData.get().getRequestReferenceId().isEmpty()) {
            providerRequestId = systemData.get().getRequestReferenceId();
        }
        builder.providerRequestId(providerRequestId);
        builder.createdOn(DateTime.now().toDate());

        if (success) {
            builder.success(true);

            Optional<CallValidateIdentityCheck> identityCheckOptional = Optional.ofNullable(transUnionResponse.getSearchResultBody().getProductResponses().getCallValidate5().getCallValidate5Response().getResponse().getResult().getCallValidateDisplays().getCallValidateIdentityCheck());
            String address = identityCheckOptional
                    .filter(callValidateIdentityCheck -> !callValidateIdentityCheck.getCurrentAddressMatched().isEmpty())
                    .map(CallValidateIdentityCheck::getCurrentAddressMatched)
                    .orElse(NOT_AVAILABLE);
            builder.address(address);

            String fullName = identityCheckOptional
                    .filter(callValidateIdentityCheck -> !callValidateIdentityCheck.getNameMatched().isEmpty())
                    .map(CallValidateIdentityCheck::getNameMatched)
                    .orElse(NOT_AVAILABLE);
            builder.lastName(fullName);

            Optional<AgeVerify> ageVerifyOptional = Optional.ofNullable(transUnionResponse.getSearchResultBody().getProductResponses().getCallValidate5().getCallValidate5Response().getResponse().getResult().getCallValidateDisplays().getAgeVerify());
            String dob = ageVerifyOptional
                    .filter(ageVerify -> !ageVerify.getDob().isEmpty())
                    .map(AgeVerify::getDob)
                    .orElse(NOT_AVAILABLE);
            builder.dob(dob);
        }
        attempt.setKycSuccessVerificationResponse(builder.build());

        kycResultsClient.addVerificationResult(attempt);
        log.debug("Transunion Verification attempt for user" + user.getGuid() + " sent to service-kyc:" + attempt);

    }
}
