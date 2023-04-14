package lithium.service.cashier.processor.paypal.api.payouts;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchHeader {

    @JsonProperty("payout_batch_id")
    private String batchId;

    @JsonProperty("batch_status")
    private String status;

    @JsonProperty("amount")
    private PayPalCurrencyObj amount;

}
