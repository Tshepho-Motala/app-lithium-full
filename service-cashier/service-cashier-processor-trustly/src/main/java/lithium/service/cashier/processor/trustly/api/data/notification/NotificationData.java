package lithium.service.cashier.processor.trustly.api.data.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class NotificationData {
    @JsonProperty("messageid")
    private String messageId;
    @JsonProperty("notificationid")
    private String notificationId;
    @JsonProperty("orderid")
    private String orderId;

    private Map<String, Object> attributes;
}
