package lithium.service.casino.provider.slotapi;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.casino.EnableCasinoClient;
import lithium.service.casino.EnableSystemBonusClient;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@LithiumService
@EnableLithiumServiceClients
@EnableJpaAuditing
@EnableDomainClient
@EnableCasinoClient
@EnableSystemBonusClient
@EnableCustomHttpErrorCodeExceptions
@EnableLimitInternalSystemClient
@EnableUserApiInternalClientService
public class ServiceCasinoProviderSlotAPIApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoProviderSlotAPIApplication.class, args);
	}
}
