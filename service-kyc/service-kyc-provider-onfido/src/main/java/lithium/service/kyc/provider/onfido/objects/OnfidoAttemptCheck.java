package lithium.service.kyc.provider.onfido.objects;

import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.onfido.entitites.CheckStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnfidoAttemptCheck {
    private String checkId;
    private String applicantId;
    private List<String> reportNames;
    private String resultsUri;
    private List<String> documentReports;
    private String userGuid;
    private String domainName;
    private CheckStatus status;
    private String comment;
    private KycSuccessVerificationResponse response;
}
