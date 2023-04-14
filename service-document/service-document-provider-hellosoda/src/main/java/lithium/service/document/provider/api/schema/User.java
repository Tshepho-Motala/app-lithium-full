package lithium.service.document.provider.api.schema;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Pattern;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @Pattern(regexp = "(?:[0-9]{2})?[0-9]{2}-[0-1]?[0-9]-[1-3]?[0-9]")
    private String birthdate;
    private Home home;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private FKContact FKContact;

    @Data
    @Builder
    public static class Home {
        private String postcode;
        private String line1;
        private String city;
        private String country;

        @JsonProperty("country_code")
        private String countryCode;
    }
}
