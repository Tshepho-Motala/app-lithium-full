package lithium.service.access.provider.gamstop;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.rest.EnableRestTemplate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lithium.client.changelog.EnableChangeLogService;

@LithiumService
@EnableRestTemplate
@EnableLithiumServiceClients
@EnableChangeLogService
@EnableDomainClient
public class ServiceAccessProviderGamstopApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceAccessProviderGamstopApplication.class, args);
	}
}
