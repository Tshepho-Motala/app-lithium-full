package lithium.service.cashier.processor.paypal.api.payouts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalCurrencyObj {

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("currency")
    private String currency;
}
