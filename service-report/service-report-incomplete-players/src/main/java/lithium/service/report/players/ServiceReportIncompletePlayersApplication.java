package lithium.service.report.players;

import lithium.leader.EnableLeaderCandidate;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;

@LithiumService
@EnableLeaderCandidate
@EnableScheduling
@EnableMailStream
@EnableDomainClient
public class ServiceReportIncompletePlayersApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceReportIncompletePlayersApplication.class, args);
	}
	
	@Override
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		
		if (isLoadTestData()) {
		}
	}
}
