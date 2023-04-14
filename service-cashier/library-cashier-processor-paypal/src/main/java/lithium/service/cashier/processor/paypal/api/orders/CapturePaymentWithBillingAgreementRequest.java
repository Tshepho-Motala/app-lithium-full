package lithium.service.cashier.processor.paypal.api.orders;

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
public class CapturePaymentWithBillingAgreementRequest {
    @JsonProperty("payment_source")
    private PaymentSource paymentSource;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class PaymentSource {
        private Token token;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @ToString
        public static class Token {
            private String id;
            private String type;
        }
    }
}
