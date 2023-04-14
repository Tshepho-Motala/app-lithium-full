package lithium.service.notifications.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface RegisterNotificationTriggerQueueSink {
    String INPUT = "register-notification-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
