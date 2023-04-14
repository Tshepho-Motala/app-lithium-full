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
public class WebhookWithdrawRequestData {
    private Integer amount;
    private String currency;
    private String domain;
    private String failures;
    private Long id;
    private WebhookIntegration integration;
    private String reason;
    private String reference;
    private String source;
    private String source_details;
    private String status;
    @JsonProperty("titan_code")
    private String titanCode;
    @JsonProperty("transfer_code")
    private String transferCode;
    @JsonProperty("transferred_at")
    private String transferredAt;
    private Recipient recipient;
    private WebhookSession session;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    private String channel;
    private String fees;
    @JsonProperty("ip_address")
    private String ipAddress;
    private Metadata metadata;

}
