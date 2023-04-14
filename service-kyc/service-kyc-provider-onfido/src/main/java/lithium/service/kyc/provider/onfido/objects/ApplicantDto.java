package lithium.service.kyc.provider.onfido.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicantDto {
    private String applicantId;
    private String sdkToken;
}
