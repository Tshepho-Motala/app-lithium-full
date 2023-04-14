package lithium.service.cashier.processor.paynl.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookData {
    private String action;
    private String amount;
    @JsonProperty("domain_id")
    private String domainId;
    @JsonProperty("enduser_id")
    private String enduserId;
    private String extra1;
    private String extra2;
    private String extra3;
    private String info;
    @JsonProperty("ip_address")
    private String ipAddress;
    private String object;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("payment_method_id")
    private String paymentMethodId;
    @JsonProperty("payment_profile_id")
    private String paymentProfileId;
    @JsonProperty("payment_session_id")
    private String paymentSessionId;
    private String pincode;
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("program_id")
    private String programId;
    @JsonProperty("promotor_id")
    private String promotorId;
    private String refundId;
    private String secret;
    private String tool;
    @JsonProperty("transfer_data")
    private String transferData;
    @JsonProperty("website_id")
    private String websiteId;
    @JsonProperty("website_location_id")
    private String websiteLocationId;
}
