package lithium.service.cashier.processor.trustly.api.data.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lithium.service.cashier.processor.trustly.api.data.Method;
import lombok.Data;

@Data
public class Notification {
    private Method method;
    private NotificationParameters params;
    private double version = 1.1;
    @JsonIgnore
    public String getUUID() {
        return params.getUuid();
    }
}
