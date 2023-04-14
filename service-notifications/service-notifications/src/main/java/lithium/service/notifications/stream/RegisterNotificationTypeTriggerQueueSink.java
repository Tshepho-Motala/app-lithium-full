package lithium.service.notifications.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface RegisterNotificationTypeTriggerQueueSink {
    String INPUT = "register-notification-type-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
