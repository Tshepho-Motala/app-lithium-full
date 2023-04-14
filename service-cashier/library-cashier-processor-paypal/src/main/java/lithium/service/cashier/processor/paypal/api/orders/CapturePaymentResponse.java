package lithium.service.cashier.processor.paypal.api.orders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paypal.api.Amount;
import lithium.service.cashier.processor.paypal.api.Link;
import lithium.service.cashier.processor.paypal.api.Payer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CapturePaymentResponse {

    private String id;
    private String status;
    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;
    private Payer payer;
    private List<Link> links;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class PurchaseUnit {
        @JsonProperty("reference_id")
        private String referenceId;
        private Payments payments;
    }

}
