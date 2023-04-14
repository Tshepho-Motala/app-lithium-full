package lithium.service.cashier.processor.checkout.cc.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreviousAttributes {
    @JsonProperty("expiry_month")
    int expiryMonth;
    @JsonProperty("expiry_year")
    int expiryYear;
    @JsonProperty("last4")
    String last4Digits;
    String bin;
    String fingerprint;
}
