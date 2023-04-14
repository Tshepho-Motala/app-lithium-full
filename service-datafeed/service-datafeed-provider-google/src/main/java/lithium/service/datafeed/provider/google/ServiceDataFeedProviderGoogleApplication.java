package lithium.service.datafeed.provider.google;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@LithiumService
@EnableLithiumServiceClients
@EnableCustomHttpErrorCodeExceptions
public class ServiceDataFeedProviderGoogleApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceDataFeedProviderGoogleApplication.class, args);
    }
}
