package lithium.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLeaderCandidate
@EnableScheduling
@EnableLithiumServiceClients
@EnableChangeLogService
@EnableTranslationsStream
@EnableMailStream
public class ServiceEntityApplication extends LithiumServiceApplication {
	@Autowired Init initStuff;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceEntityApplication.class, args);
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		if (isLoadTestData()) {
			initStuff.init();
		}
	}
}
