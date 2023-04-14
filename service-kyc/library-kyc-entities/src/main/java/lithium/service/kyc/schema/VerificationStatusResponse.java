package lithium.service.kyc.schema;

import lithium.service.limit.client.objects.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationStatusResponse {
    private String verificationStatus;
	private Long verificationStatusId;
    private Integer verificationLevel;

    public static VerificationStatusResponse newFrom(VerificationStatus verificationStatus) {
        return new VerificationStatusResponse(verificationStatus.name(), verificationStatus.getId(), verificationStatus.getLevel());
    }
}
