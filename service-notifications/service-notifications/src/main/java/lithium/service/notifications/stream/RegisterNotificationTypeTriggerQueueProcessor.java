package lithium.service.notifications.stream;

import lithium.service.notifications.services.NotificationTypeService;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@Slf4j
@EnableBinding(RegisterNotificationTypeTriggerQueueSink.class)
public class RegisterNotificationTypeTriggerQueueProcessor {

    @Autowired
    private NotificationTypeService notificationTypeService;

    @StreamListener(RegisterNotificationTypeTriggerQueueSink.INPUT)
    public void process(String type) {
        if (StringUtil.isEmpty(type)) {
            log.error("Cannot register an notification type with an empty value");
            return;
        }

        notificationTypeService.register(type);
    }
}
