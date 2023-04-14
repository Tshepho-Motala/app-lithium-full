package lithium.service.cashier.processor.paypal.api.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Payer {
    @JsonProperty("payment_method")
    private String paymentMethod;
    private String status;
    @JsonProperty("payer_info")
    private PayerInfo payerInfo;
    @JsonProperty("funding_instruments")
    private List<FundingInstrument> fundingInstruments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class FundingInstrument {
        private Billing billing;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Billing {
        @JsonProperty("billing_agreement_id")
        private String billingAgreementId;
    }
}
