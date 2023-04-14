package lithium.service.casino.provider.incentive;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.accounting.client.service.EnableAccountingClientService;
import lithium.service.casino.EnableCasinoClient;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.domain.client.util.EnableLocaleContextProcessor;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.tokens.EnableTokenUtilServices;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@LithiumService
@EnableLithiumServiceClients
@EnableJpaAuditing
@EnableDomainClient
@EnableCasinoClient
@EnableCustomHttpErrorCodeExceptions
@EnableLimitInternalSystemClient
@EnableUserApiInternalClientService
@EnableProviderClient
@EnableAccountingClientService
@EnableLocaleContextProcessor
@EnableTokenUtilServices
public class ServiceCasinoProviderIncentiveApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoProviderIncentiveApplication.class, args);
	}
}
