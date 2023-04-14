package lithium.service.notifications.services.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.services.NotificationProcessor;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(NotificationQueueSink.class)
@Slf4j
public class NotificationQueueProcessor {
	@Autowired NotificationProcessor notificationProcessor;

	@StreamListener(NotificationQueueSink.INPUT)
	public void handle(UserNotification userNotification) throws Exception {
		log.info("Received userNotification from queue for processing " + userNotification);
		notificationProcessor.process(userNotification);
	}
}