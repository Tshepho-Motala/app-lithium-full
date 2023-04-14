package lithium.service.cashier.processor.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyWebhookSignatureRequest {
    @JsonProperty("webhook_event")
    @JsonRawValue
    private String webhook;
    @JsonProperty("auth_algo")
    private String authAlgo;
    @JsonProperty("cert_url")
    private String certUrl;
    @JsonProperty("transmission_id")
    private String tranId;
    @JsonProperty("transmission_sig")
    private String tranSig;
    @JsonProperty("transmission_time")
    private String tranTime;
    @JsonProperty("webhook_id")
    private String webhookId;
}
