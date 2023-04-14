package lithium.service.cashier.processor.trustly.api.data.notification.notificationdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.trustly.api.data.notification.NotificationData;
import lombok.Data;

@Data
public class CancelNotificationData extends NotificationData {
    @JsonProperty("enduserid")
    private String endUserId;
    private String timestamp;

}
