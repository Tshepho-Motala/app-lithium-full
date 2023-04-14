package lithium.service.report.players;

import java.sql.SQLException;

import lithium.service.access.client.gamstop.EnableGamstopStream;
import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

import lithium.leader.EnableLeaderCandidate;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.service.report.players.services.TestDataService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLeaderCandidate
@EnableScheduling
@EnableMailStream
@EnableDomainClient
@EnableNotificationStream
@EnableGamstopStream
public class ServiceReportPlayersApplication extends LithiumServiceApplication {
	
	@Autowired TestDataService testDataService;

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceReportPlayersApplication.class, args);
	}
	
	@Override
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		
		if (isLoadTestData()) {
			testDataService.load();
		}
	}
}
