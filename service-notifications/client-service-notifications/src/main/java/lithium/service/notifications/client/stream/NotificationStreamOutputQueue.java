package lithium.service.notifications.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface NotificationStreamOutputQueue {
	@Output("notification-output")
	MessageChannel channel();

	@Output("register-notification-output")
	MessageChannel notificationRegisterOrUpdateChannel();

	@Output("register-notification-type-output")
	MessageChannel registerNotificationTypeChannel();
}