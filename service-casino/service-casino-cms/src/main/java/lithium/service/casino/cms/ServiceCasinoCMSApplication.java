package lithium.service.casino.cms;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.games.client.service.EnableGameUserStatusClientService;
import lithium.service.games.client.service.EnableGamesInternalSystemClientService;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableLeaderCandidate
@EnableScheduling
@EnableCustomHttpErrorCodeExceptions
@EnableJpaAuditing
@EnableLithiumServiceClients
@EnableUserApiInternalClientService
@EnableGameUserStatusClientService
@EnableGamesInternalSystemClientService
@EnableLimitInternalSystemClient
@LithiumService
public class ServiceCasinoCMSApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoCMSApplication.class, args);
	}
}
