package lithium.service.casino.provider.iforium.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    @JsonProperty("OperatorAccountID")
    private String operatorAccountId;

    @JsonProperty("CurrencyCode")
    private String currencyCode;

    @JsonProperty("CountryCode")
    private String countryCode;

    @JsonProperty("GatewaySessionToken")
    private String gatewaySessionToken;
}
