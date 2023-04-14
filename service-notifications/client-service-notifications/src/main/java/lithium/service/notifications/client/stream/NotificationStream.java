package lithium.service.notifications.client.stream;

import lithium.service.notifications.client.objects.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import lithium.service.notifications.client.objects.UserNotification;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationStream {
	@Autowired NotificationStreamOutputQueue channel;
	
	public void process(UserNotification userNotification) {
		try {
			channel.channel().send(MessageBuilder.<UserNotification>withPayload(userNotification).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}

	public void createOrUpdateNotification(Notification notification) {
		try {
			channel.notificationRegisterOrUpdateChannel().send(MessageBuilder.<Notification>withPayload(notification).build());
		}
		catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void registerNotificationType(String notificationType) {
		try {
			channel.registerNotificationTypeChannel().send(MessageBuilder.<String>withPayload(notificationType).build());
		}
		catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}
}