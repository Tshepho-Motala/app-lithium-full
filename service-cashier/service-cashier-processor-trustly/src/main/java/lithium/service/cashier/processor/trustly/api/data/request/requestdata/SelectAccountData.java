package lithium.service.cashier.processor.trustly.api.data.request.requestdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.trustly.api.data.request.RequestData;
import lombok.Data;

@Data
public class SelectAccountData extends RequestData {
    @JsonProperty("NotificationURL")
    private String notificationUrl;
    @JsonProperty("EndUserID")
    private String endUserId;
    @JsonProperty("MessageID")
    private String messageId;
}
