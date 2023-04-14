package lithium.service.user.provider.sphonic.idin.config;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Data
@Getter
public class Configuration {
    private String authenticationUrl;
    private String username;
    private String password;
    private String merchantId;
    private String iDinUrl;
    private String applicantReferenceOffset;
    private String iDinStartWorkflowName;
    private String iDinRetrieveWorkflowName;
    private Integer connectionRequestTimeout = 60000;
    private Integer connectionTimeout = 60000;
    private Integer socketTimeout = 60000;
    private String applicantHashKey = "iDinApplicantHash";
}
