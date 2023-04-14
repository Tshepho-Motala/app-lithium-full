package lithium.service.cashier.processor.paypal.api.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paypal.api.Amount;
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
public class SellerReceivableBreakdown {
    @JsonProperty("gross_amount")
    private Amount grossAmount;
    @JsonProperty("paypal_fee")
    private Amount paypalFee;
    @JsonProperty("net_amount")
    private Amount netAmount;

}
