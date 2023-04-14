package lithium.service.access.provider.sphonic.kyc.services;

import lithium.service.access.provider.sphonic.schema.kyc.response.SphonicKYCResponse;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.kyc.client.KycResultsClient;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.objects.VendorData;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VerificationResultService {
    public static final String SPHONIC_KYC = "SphonicKYC";
    public static final String METHOD_NAME = "SphonicKYC";
    public static final String NOT_AVAILABLE = "Not available";
    @Autowired
    LithiumServiceClientFactory services;

    public void sendVerificationResult(SphonicKYCResponse sphonicKYCResponse, User user, boolean success) {
        try {
            log.debug("Requested attempt to save the result of the transunion kyc verification result for user:" + user.getGuid());
            KycResultsClient kycResultsClient = services.target(KycResultsClient.class, "service-kyc", true);
            VerificationKycAttempt attempt = VerificationKycAttempt.builder()
                .userGuid(user.guid())
                .domainName(user.getDomain().getName())
                .providerName(SPHONIC_KYC)
                .methodName(METHOD_NAME)
                .build();

            KycSuccessVerificationResponse.KycSuccessVerificationResponseBuilder builder = KycSuccessVerificationResponse.builder();
            builder.resultMessageText("Verification result:" + sphonicKYCResponse.getFinalResult().toUpperCase()
                + (sphonicKYCResponse.getAgeVerified() != null ? ", AgeVerified: " + sphonicKYCResponse.getAgeVerified().toString().toUpperCase() : "")
                + (sphonicKYCResponse.getAddressVerified() != null ? ", AddressVerified: " + sphonicKYCResponse.getAddressVerified().toString().toUpperCase() : ""));
            builder.success(success);
            builder.providerRequestId(sphonicKYCResponse.getSphonicResponse().getData().getTransactionDetails().getTransactionId());
            builder.createdOn(DateTime.now().toDate());

            builder.dob(BooleanUtils.isTrue(sphonicKYCResponse.getAgeVerified()) ? user.getDateOfBirth().toString() : NOT_AVAILABLE);
            builder.address(BooleanUtils.isTrue(sphonicKYCResponse.getAddressVerified()) ? user.getResidentialAddress().getAddressLine1() : NOT_AVAILABLE);
            builder.lastName(sphonicKYCResponse.getSphonicResponse().getData().getKycResponse().getSummary().getDetails().getSurname());

            builder.vendorsData(getVendorData(sphonicKYCResponse));
            attempt.setKycSuccessVerificationResponse(builder.build());
            kycResultsClient.addVerificationResult(attempt);
            log.debug("Sphonic KYC Verification attempt sent to service-kyc:" + attempt);
        } catch (Exception e) {
            log.error("Cant store Sphonic KYC verification result: " + sphonicKYCResponse + " for user:" + user.getGuid() +" Exception:" + Arrays.toString(e.getStackTrace()));
        }
    }

    private List<VendorData> getVendorData(SphonicKYCResponse sphonicKYCResponse) {
        return Optional.ofNullable(sphonicKYCResponse.getSphonicResponse().getData().getKycResponse().getVendorSpecificKycResult())
            .map(kycResult -> Arrays.stream(kycResult.getVendorResponse())
                                    .filter(vr -> vr.get("vendorCalled") != null && !vr.get("vendorCalled").equalsIgnoreCase("none"))
                                    .map(vr-> VendorData.builder().name(vr.get("vendorCalled")).data(vr).build())
                                    .collect(Collectors.toList()))
            .orElse(null);

    }
}
