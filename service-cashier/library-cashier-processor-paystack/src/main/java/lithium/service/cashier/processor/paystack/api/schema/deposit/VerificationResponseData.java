package lithium.service.cashier.processor.paystack.api.schema.deposit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paystack.api.schema.AuthorizationData;
import lithium.service.cashier.processor.paystack.api.schema.Metadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerificationResponseData {
    private Long id;
    private String status;
    private String reference;
    private BigDecimal amount;
    @JsonProperty("gateway_response")
    private String  gatewayResponse;
    @JsonProperty("paid_at")
    private String paidAt;
    private String channel;
    private String currency;
    @JsonProperty("ip_address")
    private String ipAddress;
    private Double fees;
    @JsonProperty("fees_split")
    private String feesSplit;
    private Object metadata;
    private Customer customer;
    @JsonProperty("created_at")
    private String createdAt;
    private AuthorizationData authorization;
}
