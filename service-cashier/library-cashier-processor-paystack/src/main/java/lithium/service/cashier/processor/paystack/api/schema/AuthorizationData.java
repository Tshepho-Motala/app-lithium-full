package lithium.service.cashier.processor.paystack.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationData {
    @JsonProperty("authorization_code")
    private String authorizationCode;
    @JsonProperty("card_type")
    private String cardType;//scheme
    @JsonProperty("last4")
    private String lastFour;
    @JsonProperty("exp_month")
    private int expiryMonth;
    @JsonProperty("exp_year")
    private int expiryYear;
    private String bin;
    private String bank;
    private String channel;
    private String signature;//reference
    private Boolean reusable;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("account_name")
    private String accountName;
    private String brand;
}
