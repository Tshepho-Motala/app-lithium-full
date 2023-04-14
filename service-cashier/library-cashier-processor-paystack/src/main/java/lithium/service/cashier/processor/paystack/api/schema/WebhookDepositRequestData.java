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
public class WebhookDepositRequestData {
    private Long amount;
    private AuthorizationData authorization;
    private String channel;
    @JsonProperty("created_at")
    private String createdAt;
    private String currency;
    private String domain;
    private String fees;
    @JsonProperty("fees_split")
    private Object feesSplit;
    @JsonProperty("gateway_response")
    private String gatewayResponse;
    private Long id;
    @JsonProperty("ip_address")
    private String ipAddress;
    private Object log;
    private String message;
    private Metadata metadata;
    @JsonProperty("order_id")
    private String orderId;
    private String paidAt;
    private String paid_at;
    private Object plan;
    private String reference;
    @JsonProperty("requested_amount")
    private Long requestedAmount;
    private Object split;
    private String status;
    private Object subaccount;

}
