package lithium.service.cashier.processor.paystack.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackUSSDData {
    private BigDecimal amount;
    private String reference;
    private String status;
    private String currency;
    private String channel;
    @JsonProperty("ip_address")
    private String ipAddress;
    private String fees;
    private UssdDepositChargeRequestMetadata metadata;
}
