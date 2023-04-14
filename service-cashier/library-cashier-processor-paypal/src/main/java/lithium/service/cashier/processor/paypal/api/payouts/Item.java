package lithium.service.cashier.processor.paypal.api.payouts;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paypal.api.Link;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    @JsonProperty("payout_item_id")
    private String payoutItemId;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("activity_id")
    private String activityId;
    @JsonProperty("transaction_status")
    private String transactionStatus;
    @JsonProperty("payout_item_fee")
    private Object payout_item_fee;
    @JsonProperty("payout_batch_id")
    private String batchId;
    @JsonProperty("payout_item")
    private PayoutItem payoutItem;
    @JsonProperty("time_processed")
    private String timeProcessed;
    private List<Link> links;

}
