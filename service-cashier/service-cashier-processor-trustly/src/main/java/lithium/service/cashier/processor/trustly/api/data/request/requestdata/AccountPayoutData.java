package lithium.service.cashier.processor.trustly.api.data.request.requestdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.trustly.api.data.request.RequestData;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountPayoutData extends RequestData {
    @JsonProperty("NotificationURL")
    private String notificationUrl;
    @JsonProperty("EndUserID")
    private String enduserId;
    @JsonProperty("AccountID")
    private String accountId;
    @JsonProperty("MessageID")
    private String messageId;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("Amount")
    private String amount;
}
