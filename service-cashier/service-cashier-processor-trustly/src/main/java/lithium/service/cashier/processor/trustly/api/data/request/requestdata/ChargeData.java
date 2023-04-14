package lithium.service.cashier.processor.trustly.api.data.request.requestdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.trustly.api.data.Currency;
import lithium.service.cashier.processor.trustly.api.data.request.RequestData;
import lombok.Data;

@Data
public class ChargeData extends RequestData {
    @JsonProperty("AccountID")
    private String accountID;
    @JsonProperty("NotificationURL")
    private String notificationURL;
    @JsonProperty("EndUserID")
    private String endUserID;
    @JsonProperty("MessageID")
    private String messageID;
    @JsonProperty("Amount")
    private String amount;
    @JsonProperty("Currency")
    private Currency currency;
}
