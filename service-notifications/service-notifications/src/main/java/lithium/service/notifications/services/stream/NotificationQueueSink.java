package lithium.service.notifications.services.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface NotificationQueueSink {
	String INPUT = "notification-input";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}