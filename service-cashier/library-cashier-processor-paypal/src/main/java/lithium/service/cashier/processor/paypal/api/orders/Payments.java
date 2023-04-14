package lithium.service.cashier.processor.paypal.api.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paypal.api.Amount;
import lithium.service.cashier.processor.paypal.api.Link;
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
public class Payments {
    private List<Capture> captures;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Capture {
        private String id;
        private String status;
        private Amount amount;
        @JsonProperty("final_capture")
        private Boolean finalCapture;
        @JsonProperty("seller_protection")
        private Object sellerProtection;
        @JsonProperty("seller_receivable_breakdown")
        private SellerReceivableBreakdown sellerReceivableBreakdown;
        private List<Link> links;
        @JsonProperty("create_time")
        private String createTime;
        @JsonProperty("update_time")
        private String updateTime;
    }
}
