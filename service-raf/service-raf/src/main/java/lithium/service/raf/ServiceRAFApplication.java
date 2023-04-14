package lithium.service.raf;

import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import lithium.service.casino.client.stream.EnableTriggerBonusStream;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.promo.client.stream.EnableMissionStatsStream;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.service.stats.client.stream.EnableStatsStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLithiumServiceClients
@EnableStatsStream
@EnableNotificationStream
@EnableTriggerBonusStream
@EnableMissionStatsStream
public class ServiceRAFApplication extends LithiumServiceApplication {
	@Autowired
	private Init initStuff;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceRAFApplication.class, args);
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		if (isLoadTestData()) {
			initStuff.init();
		}
	}
}
