package lithium.service.cashier.processor.trustly.api.data.notification;

import lombok.Data;

@Data
public class NotificationParameters {
    private String signature;
    private String uuid;
    private NotificationData data;
}
