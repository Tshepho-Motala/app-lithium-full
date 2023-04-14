package lithium.service.report.games;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

import lithium.leader.EnableLeaderCandidate;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.service.report.games.services.TestDataService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLeaderCandidate
@EnableScheduling
@EnableMailStream
@EnableDomainClient
public class ServiceReportGamesApplication extends LithiumServiceApplication {
	@Autowired TestDataService testDataService;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceReportGamesApplication.class, args);
	}
	
	@Override
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		
		if (isLoadTestData()) {
			testDataService.load();
		}
	}
}
