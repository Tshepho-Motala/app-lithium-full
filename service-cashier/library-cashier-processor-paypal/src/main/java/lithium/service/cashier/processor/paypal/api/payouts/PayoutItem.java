package lithium.service.cashier.processor.paypal.api.payouts;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class PayoutItem {

    @JsonProperty("amount")
    private PayPalCurrencyObj amount;

    @JsonProperty("receiver")
    private String receiver;

    @JsonProperty("recipient_type")
    private String recipientType;

    @JsonProperty("sender_item_id")
    private String senderItemId;
}
