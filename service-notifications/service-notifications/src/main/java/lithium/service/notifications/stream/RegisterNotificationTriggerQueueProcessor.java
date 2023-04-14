package lithium.service.notifications.stream;

import lithium.service.notifications.client.objects.Notification;
import lithium.service.notifications.services.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@Slf4j
@EnableBinding(RegisterNotificationTriggerQueueSink.class)
public class RegisterNotificationTriggerQueueProcessor {
    @Autowired
    private NotificationService notificationService;

    @StreamListener(RegisterNotificationTriggerQueueSink.INPUT)
    public void process(Notification notification) {
        try {
            log.debug("Received notification for processing, notification: " + notification);
            notificationService.createOrUpdate(notification);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
