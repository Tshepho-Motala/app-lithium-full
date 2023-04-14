package lithium.service.cashier.processor.hexopay.api.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PayoutRequest {
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
            @JsonProperty("exp_month")
            private String expMonth;
            @JsonProperty("exp_year")
            private String expYear;
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
        private boolean test;
        @JsonProperty("recipient_credit_card")
        private CreditCard creditCard;
        private Customer recipient;
        @JsonProperty("recipient_billing_address")
        private BillingAddress billingAddress;
    }
    private Request request;
}
