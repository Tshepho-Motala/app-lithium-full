package lithium.service.cashier.processor.hexopay.api.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.hexopay.api.gateway.data.AdditionData;
import lithium.service.cashier.processor.hexopay.api.gateway.data.BillingAddress;
import lithium.service.cashier.processor.hexopay.api.gateway.data.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Request {
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class CreditCard {
            private String holder;
            private String token;
            @JsonProperty("skip_three_d_secure_verification")
            private boolean skipThreeD;
            @JsonProperty("verification_value")
            private String cvv;
        }

        private Long amount;
        private String currency;
        private String description;
        @JsonProperty("tracking_id")
        private String trackingId;
        @JsonProperty("expired_at")
        private String expiredAt;
        private String language;
        @JsonProperty("notification_url")
        private String notificationUrl;
        @JsonProperty("verification_url")
        private String verification_url;
        @JsonProperty("return_url")
        private String returnUrl;
        private boolean test;
        @JsonProperty("credit_card")
        private CreditCard creditCard;
        @JsonProperty("addition_data")
        private AdditionData additionData;
        private Customer customer;
        @JsonProperty("billing_address")
        private BillingAddress billingAddress;
    }
    private Request request;
}
