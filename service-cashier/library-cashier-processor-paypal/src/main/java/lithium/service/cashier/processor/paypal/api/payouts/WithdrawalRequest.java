package lithium.service.cashier.processor.paypal.api.payouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WithdrawalRequest {

    @JsonProperty("items")
    private List<PayoutItem> items;

    @JsonProperty("sender_batch_header")
    private SenderBatchHeader senderBatchHeader;
}
