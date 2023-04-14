package lithium.service.cashier.processor.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifyWebhookSignatureResponse {
    @JsonProperty("verification_status")
    private String verificationStatus;
}
