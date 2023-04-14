package lithium.service.access.provider.sphonic.schema.kyc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorSpecificKycResult {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VendorResponse {
        private String vendorCalled;
        private String vendorReference;
        private String residencyMatch;
        private String dobMatch;
        private String over18;
        private String concatSources;
        private String numSources;
        private String onePlusOne;
        private String twoPlusTwo;
        private String deceasedFlag;
        private String nameAddressDob;
        private String fullVerification;
        private String numResidencyMatches;
        private String numDobMatches;
        private String regKycScore;
        private String gamKycScore;
        private String callOrder;
    }
    private Map<String, String>[] vendorResponse;
}
