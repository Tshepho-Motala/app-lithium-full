package lithium.service.cashier.processor.paypal.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payer {
    private Name name;
    @JsonProperty("email_address")
    private String emailAddress;
    @JsonProperty("payer_id")
    private String payerId;
    private Address address;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Name {
        @JsonProperty("given_name")
        private String givenName;
        private String surname;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Address {
		@JsonProperty("address_line_1")
		private String addressLine1;
		@JsonProperty("address_line_2")
		private String addressLine2;
		@JsonProperty("admin_area_1")
		private String adminArea1;
		@JsonProperty("admin_area_2")
		private String adminArea2;
		@JsonProperty("postal_code")
		private String postalCode;
		@JsonProperty("country_code")
		private String countryCode;
    }
}
