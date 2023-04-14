package lithium.service.report.player.trans;

import java.sql.SQLException;

import lithium.service.casino.EnableCasinoClient;
import lithium.service.domain.client.EnableDomainClient;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLeaderCandidate
@EnableScheduling
@EnableLithiumServiceClients
@EnableCasinoClient
@EnableDomainClient
public class ServiceReportPlayerTransApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceReportPlayerTransApplication.class, args);
	}
}
