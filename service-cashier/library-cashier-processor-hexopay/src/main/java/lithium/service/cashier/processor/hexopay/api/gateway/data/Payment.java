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
public class Payment {
    @JsonProperty("auth_code")
    private String authCode;
    @JsonProperty("bank_code")
    private String bankCode;
    private String rrn;
    @JsonProperty("ref_id")
    private String refId;
    private String message;
    private Long amount;
    private String currency;
    @JsonProperty("billing_descriptor")
    private String billingDescriptor;
    @JsonProperty("gateway_id")
    private String gatewayId;
    private String status;
}
