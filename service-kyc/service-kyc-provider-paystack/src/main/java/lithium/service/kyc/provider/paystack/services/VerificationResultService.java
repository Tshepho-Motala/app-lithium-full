package lithium.service.kyc.provider.paystack.services;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.kyc.client.KycResultsClient;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@AllArgsConstructor
public class VerificationResultService {
    public static final String PROVIDER_NAME = "Paystack";
    public static final String METHOD_NAME = "BVN Check";
    public static final String NOT_AVAILABLE = "Not available";

    private final LithiumServiceClientFactory services;

    public void sendVerificationAttempt(BvnResolveResponse responseData, String userGuid, boolean manual, String comment) {
        log.debug("Requested attempt to save the result of the Paystack BVN Check kyc verification result for user:" + userGuid);
        try {
            KycResultsClient kycResultsClient = services.target(KycResultsClient.class, "service-kyc", true);

            String[] userDomain = userGuid.split("/");
            String domainName = userDomain[0];
            VerificationKycAttempt attempt = VerificationKycAttempt.builder()
                    .userGuid(userGuid)
                    .domainName(domainName)
                    .providerName(PROVIDER_NAME)
                    .methodName(METHOD_NAME)
                    .build();

            KycSuccessVerificationResponse.KycSuccessVerificationResponseBuilder builder = KycSuccessVerificationResponse.builder();
            builder.resultMessageText(Optional.ofNullable(responseData).map(BvnResolveResponse::getMessage).orElse(""));
            if (nonNull(comment)) {
                builder.resultMessageText(comment);
            }
            builder.createdOn(DateTime.now().toDate());
            builder.manual(manual);
            builder.providerRequestId(NOT_AVAILABLE);

            String lastName = NOT_AVAILABLE;
            String dob = NOT_AVAILABLE;
            if (nonNull(responseData) && "true".equalsIgnoreCase(responseData.getStatus())) {
                builder.success(true);
                builder.methodTypeUid(responseData.getData().getBvn());
                if (responseData.getData().getLastName() != null) {
                    lastName = responseData.getData().getLastName();
                }
                if (responseData.getData().getDob() != null) {
                    dob = responseData.getData().getDob();
                }
            }
            builder.lastName(lastName);
            builder.dob(dob);

            attempt.setKycSuccessVerificationResponse(builder.build());
            kycResultsClient.addVerificationResult(attempt);
            log.debug("Paystack BVN Check  Verification attempt for user" + userGuid + " sent to service-kyc:" + attempt);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Cant store PaystackBVN verification result ", e);
        }
    }


}
