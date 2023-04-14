package lithium.service.casino.provider.sportsbook;

import java.util.List;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.accounting.client.service.EnableAccountingClientService;
import lithium.service.cashier.client.service.EnableCashierInternalClientService;
import lithium.service.casino.EnableCasinoClient;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.user.client.service.EnableLoginEventClientService;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 *
 */
@LithiumService
@EnableLithiumServiceClients
@EnableLeaderCandidate
@EnableJpaAuditing
@EnableDomainClient
@EnableCasinoClient
@EnableCustomHttpErrorCodeExceptions
@EnableLimitInternalSystemClient
@EnableLoginEventClientService
@EnableUserApiInternalClientService
@EnableProviderClient
@EnableCashierInternalClientService
@EnableAccountingClientService
@EnableChangeLogService
@EnableRestTemplate
@EnableScheduling
public class ServiceCasinoProviderSportsbookApplication extends LithiumServiceApplication {

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoProviderSportsbookApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(@Qualifier("lithium.rest") RestTemplateBuilder builder) {
		return builder.build();
	}

}
