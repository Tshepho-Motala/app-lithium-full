package lithium.service.cashier.processor.trustly.api.data.notification.notificationdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.trustly.api.data.notification.NotificationData;
import lombok.Data;

@Data
public class AccountNotificationData extends NotificationData {
    @JsonProperty("accountid")
    private String accountId;
    private char verified;
}
