package lithium.service.cashier.processor.trustly.api.data.request.requestdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.trustly.api.data.request.RequestData;
import lombok.Data;

@Data
public class DepositData extends RequestData {
    @JsonProperty("NotificationURL")
    private String notificationURL;
    @JsonProperty("EndUserID")
    private String endUserID;
    @JsonProperty("MessageID")
    private String messageID;
}
