package lithium.service.cashier.processor.hexopay.api.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.hexopay.api.page.data.Customer;
import lithium.service.cashier.processor.hexopay.api.page.data.Order;
import lithium.service.cashier.processor.hexopay.api.page.data.Settings;
import lithium.service.cashier.processor.hexopay.api.page.data.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentTokenRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Checkout {
        @JsonProperty(defaultValue = "2.1")
        private String version;
        private boolean test;
        @JsonProperty("transaction_type")
        private TransactionType transactionType;
        private Integer attempts;
        @JsonProperty("tracking_id")
        private String trackingId;
        @JsonProperty("dynamic_billing_descriptor")
        private String billingDescriptor;
        private Order order;
        private Settings settings;
        private Customer customer;
    }
    private Checkout checkout;
}
