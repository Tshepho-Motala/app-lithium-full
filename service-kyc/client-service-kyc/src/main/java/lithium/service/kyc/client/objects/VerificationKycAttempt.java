package lithium.service.kyc.client.objects;

import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationKycAttempt {
    private String userGuid;
    private String domainName;
    private String providerName;
    private String methodName;
    private Long verificationResultId;
    private KycSuccessVerificationResponse kycSuccessVerificationResponse;

}
