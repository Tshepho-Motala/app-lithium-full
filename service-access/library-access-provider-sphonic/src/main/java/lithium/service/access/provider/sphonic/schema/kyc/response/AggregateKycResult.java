package lithium.service.access.provider.sphonic.schema.kyc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregateKycResult {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
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
        private String finalRt;
    }
    private Result result;
}
