package lithium.service.cashier.processor.hexopay.api.gateway.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditCard {
    private String holder;
    private String stamp;
    private String brand;
    @JsonProperty("last_4")
    private String last4Digits;
    @JsonProperty("first_1")
    private String firstDigit;
    private String bin;
    @JsonProperty("issuer_country")
    private String issuerCountry;
    @JsonProperty("issuer_name")
    private String issuerName;
    private String product;
    @JsonProperty("exp_month")
    private Integer expMonth;
    @JsonProperty("exp_year")
    private Integer expYear;
    @JsonProperty("token_provider")
    private String tokenProvider;
    private String token;
}

