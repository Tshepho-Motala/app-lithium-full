package lithium.service.avatar;

import lithium.application.LithiumShutdownSpringApplication;

import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.promo.client.stream.EnableMissionStatsStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLithiumServiceClients
@EnableMissionStatsStream
@EnableLeaderCandidate
public class ServiceAvatarApplication extends LithiumServiceApplication {
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceAvatarApplication.class, args);
	}
	
}
