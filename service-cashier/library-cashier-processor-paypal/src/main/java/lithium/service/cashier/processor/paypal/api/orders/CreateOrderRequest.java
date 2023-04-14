package lithium.service.cashier.processor.paypal.api.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paypal.api.Amount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateOrderRequest {
    private String intent;
    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;
    @JsonProperty("application_context")
    private ApplicationContext applicationContext;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class PurchaseUnit {
        @JsonProperty("reference_id")
        private String referenceId;
        private String description;
        private Amount amount;
	    @JsonProperty("invoice_id")
        private Long invoiceId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class ApplicationContext {
        @JsonProperty("brand_name")
        private String brandName;
        @JsonProperty("landing_page")
        private String landingPage;
        @JsonProperty("shipping_preference")
        private String shippingPreference;
        @JsonProperty("user_action")
        private String userAction;
        @JsonProperty("return_url")
        private String returnUrl;
        @JsonProperty("cancel_url")
        private String cancelUrl;
    }
}
