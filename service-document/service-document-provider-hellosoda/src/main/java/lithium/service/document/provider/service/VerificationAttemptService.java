package lithium.service.document.provider.service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.kyc.client.KycResultsClient;
import lithium.service.kyc.client.exceptions.Status459VerificationResultNotFountException;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@AllArgsConstructor
public class VerificationAttemptService {

    public static final String NOT_AVAILABLE = "Not available";

    private final LithiumServiceClientFactory services;

    public VerificationResult registerFailedAttempt(String userGuid, String domainName, String jobId, String providerName, String methodName, String comment) throws LithiumServiceClientFactoryException {
        KycResultsClient kycResultsClient = services.target(KycResultsClient.class, "service-kyc", true);

        KycSuccessVerificationResponse verificationResponse = KycSuccessVerificationResponse.builder()
                .createdOn(new Date())
                .success(false)
                .manual(true)
                .providerRequestId(jobId)
                .countryOfBirth(NOT_AVAILABLE)
                .dobYearOnly(false)
                .phoneNumber(NOT_AVAILABLE)
                .resultMessageText(comment)
                .build();

        VerificationKycAttempt attempt = VerificationKycAttempt.builder()
                .userGuid(userGuid)
                .domainName(domainName)
                .providerName(providerName)
                .methodName(methodName)
                .kycSuccessVerificationResponse(verificationResponse)
                .build();

        log.debug("KYC HelloSoda verification attempt:" + attempt + " added for user " + userGuid);
        return kycResultsClient.addVerificationResult(attempt).getData();
    }

    public VerificationResult registerKYCAttempt(String userGuid, String domainName, String jobId, String providerName, String methodName) throws LithiumServiceClientFactoryException {
        KycResultsClient kycResultsClient = services.target(KycResultsClient.class, "service-kyc", true);

        KycSuccessVerificationResponse verificationResponse = KycSuccessVerificationResponse.builder()
                .createdOn(new Date())
                .success(false)
                .manual(true)
                .address(NOT_AVAILABLE)
                .providerRequestId(jobId)
                .countryOfBirth(NOT_AVAILABLE)
                .dobYearOnly(false)
                .phoneNumber(NOT_AVAILABLE)
                .resultMessageText("Not completed job")
                .build();

        VerificationKycAttempt attempt = VerificationKycAttempt.builder()
                .userGuid(userGuid)
                .domainName(domainName)
                .providerName(providerName)
                .methodName(methodName)
                .kycSuccessVerificationResponse(verificationResponse)
                .build();

        log.debug("KYC HelloSoda verification attempt:" + attempt + " added for user " + userGuid);
        return kycResultsClient.addVerificationResult(attempt).getData();
    }

    public void updateVerificationResult(VerificationKycAttempt attempt, String userGuid) throws LithiumServiceClientFactoryException, Status459VerificationResultNotFountException {
        log.debug("Requested attempt to update the result of the hellosoda " + attempt + " verification result for user:" + userGuid);
        KycResultsClient kycResultsClient = services.target(KycResultsClient.class, "service-kyc", true);

        kycResultsClient.updVerificationResult(attempt);
    }
}
