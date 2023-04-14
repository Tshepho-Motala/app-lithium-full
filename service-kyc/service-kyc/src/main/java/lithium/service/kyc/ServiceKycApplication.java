package lithium.service.kyc;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.kyc.service.UpdateVerificationStatusService;
import lithium.service.stats.client.service.EnableStatsClientService;
import lithium.service.stats.client.stream.EnableStatsStream;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@LithiumService
@EnableEurekaClient
@EnableLithiumServiceClients
@EnableUserApiInternalClientService
@EnableCustomHttpErrorCodeExceptions
@EnableProviderClient
@EnableDomainClient
@EnableStatsStream
@EnableStatsClientService
@EnableLeaderCandidate
public class ServiceKycApplication extends LithiumServiceApplication {
    @Autowired
    UpdateVerificationStatusService verificationService;

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceKycApplication.class, args);
    }

    @EventListener
    public void startup(ApplicationStartedEvent e) throws Exception {
        super.startup(e);
        verificationService.setupVerificationMethodTypesFromEnum();
    }
}
