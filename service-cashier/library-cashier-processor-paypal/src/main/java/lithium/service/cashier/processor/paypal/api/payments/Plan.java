package lithium.service.cashier.processor.paypal.api.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Plan {
    private String type;
    @JsonProperty("merchant_preferences")
    private MerchantPreferences merchantPreferences;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MerchantPreferences {
        @JsonProperty("return_url")
        private String returnUrl;
        @JsonProperty("cancel_url")
        private String cancelUrl;
        @JsonProperty("notify_url")
        private String notifyUrl;
        @JsonProperty("accepted_pymt_type")
        private String acceptedPymtType;
        @JsonProperty("skip_shipping_address")
        private Boolean skipShippingAddress;
        @JsonProperty("req_billing_address")
        private Boolean reqBillingAddress;
        @JsonProperty("immutable_shipping_address")
        private Boolean immutableShippingAddress;
    }
}
