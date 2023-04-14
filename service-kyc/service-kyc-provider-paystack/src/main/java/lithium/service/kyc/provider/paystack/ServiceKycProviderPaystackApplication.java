package lithium.service.kyc.provider.paystack;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.rest.EnableRestTemplate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.kyc.provider.paystack.config.PaystackConfigurationProperties;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@LithiumService
@EnableRestTemplate
@EnableChangeLogService
@EnableLithiumServiceClients
@EnableUserApiInternalClientService
@EnableCustomHttpErrorCodeExceptions
@EnableConfigurationProperties(PaystackConfigurationProperties.class)
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ServiceKycProviderPaystackApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceKycProviderPaystackApplication.class, args);
	}
}
