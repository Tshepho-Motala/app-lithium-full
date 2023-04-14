package lithium.service.notifications;

import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.notifications.enums.NotificationType;
import lithium.service.notifications.services.NotificationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.service.notifications.data.entities.Channel;
import lithium.service.notifications.services.ChannelService;
import lithium.service.pushmsg.client.stream.EnablePushMsgStream;
import lithium.service.sms.client.stream.EnableSMSStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableLithiumServiceClients
@EnableMailStream
@EnableSMSStream
@EnableGatewayExchangeStream
@EnablePushMsgStream
@EnableLeaderCandidate
@EnableChangeLogService
@EnableCustomHttpErrorCodeExceptions
@EnableScheduling
public class ServiceNotificationsApplication extends LithiumServiceApplication {
	@Autowired
	private Init initStuff;
	
	@Autowired
	private ChannelService channelService;

	@Autowired
	private NotificationTypeService notificationTypeService;

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceNotificationsApplication.class, args);
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		
		addChannels();
		registerNotificationTypes();

		if (isLoadTestData()) {
			initStuff.init();
		}
	}
	
	private void addChannels() {
		channelService.findOrCreate(Channel.CHANNEL_SMS);
		channelService.findOrCreate(Channel.CHANNEL_EMAIL);
		channelService.findOrCreate(Channel.CHANNEL_PUSH);
		channelService.findOrCreate(Channel.CHANNEL_PULL);
	}

	private void registerNotificationTypes() {
		notificationTypeService.register(NotificationType.DEFAULT.getType());
	}
}
