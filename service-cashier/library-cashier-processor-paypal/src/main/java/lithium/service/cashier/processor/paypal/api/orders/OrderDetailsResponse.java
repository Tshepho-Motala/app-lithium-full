package lithium.service.cashier.processor.paypal.api.orders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paypal.api.Amount;
import lithium.service.cashier.processor.paypal.api.Link;
import lithium.service.cashier.processor.paypal.api.Payer;
import lithium.service.cashier.processor.paypal.api.webhook.WebhookRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class OrderDetailsResponse {
    @JsonProperty("create_time")
    private String createTime;
    private String id;
    private String intent;
    private String status;
    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;
    private List<Link> links;
    private Payer payer;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class PurchaseUnit {
        @JsonProperty("reference_id")
        private String referenceId;
        private AmountWithBreakdown amount;
        private Payee payee;
        private String description;
        private Object shipping;
        private Object payments;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class AmountWithBreakdown {
        @JsonProperty("currency_code")
        private String currencyCode;
        private BigDecimal value;
        private Breakdown breakdown;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Payee {
        @JsonProperty("email_address")
        private String emailAddress;
        @JsonProperty("merchant_id")
        private String merchantId;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Breakdown {
        @JsonProperty("item_total")
        private Amount itemTotal;
        private Amount shipping;
        private Amount handling;
        private Amount insurance;
        @JsonProperty("shipping_discount")
        private Amount shippingDiscount;
        private Amount discount;
    }
}
