package lithium.service.cashier.processor.checkout.cc.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class CheckoutSourceUpdateWebhookData {
    String id;
    String type;
    @JsonProperty("expiry_month")
    int expiryMonth;
    @JsonProperty("expiry_year")
    int expiryYear;
    @JsonProperty("last4")
    String last4Digits;
    String bin;
    String fingerprint;
    @JsonProperty("previous_attributes")
    PreviousAttributes previousAttributes;
}

