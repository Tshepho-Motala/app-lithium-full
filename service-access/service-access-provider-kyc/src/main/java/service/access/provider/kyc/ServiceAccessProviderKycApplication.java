package service.access.provider.kyc;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@LithiumService
@EnableLithiumServiceClients
@EnableProviderClient
@EnableUserApiInternalClientService
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ServiceAccessProviderKycApplication extends LithiumServiceApplication {
    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceAccessProviderKycApplication.class, args);
    }
}
