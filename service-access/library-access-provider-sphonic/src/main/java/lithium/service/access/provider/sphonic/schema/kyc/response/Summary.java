package lithium.service.access.provider.sphonic.schema.kyc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Summary {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DateOfBirth {
        private String dobDay;
        private String dobMonth;
        private String dobYear;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Details {
        private String identity;
        private String forename;
        private String surname;
    }

    private Summary.Details details;
    private Summary.DateOfBirth dateOfBirth;
}
