package lithium.service.access.provider.kycgbg.services;

import lithium.service.access.provider.kycgbg.config.GbgResponseData;
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

@Slf4j
@Service
public class VerificationResultService {
    public static final String PROVIDER_NAME = "GBG(ID3Global)";
    public static final String METHOD_NAME = "AuthenticateSP";
    public static final String NOT_AVAILABLE = "Not available";
    @Autowired
    LithiumServiceClientFactory services;

    public void sendVerificationAttempt(GbgResponseData responseData, User user, boolean success, String message) throws LithiumServiceClientFactoryException {
        log.debug("Requested attempt to save the result of the GBG kyc verification result for user:"+ user.getGuid());
        KycResultsClient kycResultsClient = services.target(KycResultsClient.class, "service-kyc", true);
        VerificationKycAttempt attempt = VerificationKycAttempt.builder()
                .userGuid(user.guid())
                .domainName(user.getDomain().getName())
                .providerName(PROVIDER_NAME)
                .methodName(METHOD_NAME)
                .build();

        KycSuccessVerificationResponse.KycSuccessVerificationResponseBuilder builder = KycSuccessVerificationResponse.builder();
        builder.resultMessageText("verification result:" + message + " Score:" + responseData.getScorePoints());
        builder.success(success);
        builder.providerRequestId(responseData.getProviderRequestId());
        builder.createdOn(DateTime.now().toDate());

        String address = NOT_AVAILABLE;
        String lastName = NOT_AVAILABLE;
        String dob = NOT_AVAILABLE;
        if (responseData.isLastNameMatched()) {
            lastName = user.getLastName();
        }
        if (responseData.isDobMatched()) {
            dob = user.getDateOfBirth().toString();
        }
        if (responseData.isAddressMatched()) {
            address = user.getResidentialAddress().getAddressLine1();
        }
        builder.dob(dob);
        builder.address(address);
        builder.lastName(lastName);

        attempt.setKycSuccessVerificationResponse(builder.build());
        kycResultsClient.addVerificationResult(attempt);
        log.debug("GBG Verification attempt sent to service-kyc:" + attempt );
    }
}
