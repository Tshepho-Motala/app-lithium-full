package lithium.service.casino.provider.iforium;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.modules.ModuleInfo;
import lithium.rest.EnableRestTemplate;
import lithium.service.casino.EnableCasinoClient;
import lithium.service.casino.provider.iforium.enums.IForiumGameSuppliers;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.util.EnableLocaleContextProcessor;
import lithium.service.games.client.progressivejackpotfeedregister.EnableProgressiveJackpotFeedRegistrationService;
import lithium.service.games.client.progressivejackpotfeedregister.ProgressiveJackpotFeedRegistrationService;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.reward.client.EnableQueryRewardClient;
import lithium.service.user.client.service.EnableLoginEventClientService;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@LithiumService
@EnableLithiumServiceClients
@EnableDomainClient
@EnableCasinoClient
@EnableRestTemplate
@EnableLoginEventClientService
@EnableCustomHttpErrorCodeExceptions
@EnableLimitInternalSystemClient
@EnableUserApiInternalClientService
@EnableQueryRewardClient
@EnableLocaleContextProcessor
@EnableProgressiveJackpotFeedRegistrationService
@ComponentScan(basePackages = "lithium.service.casino.provider")
@EnableConfigurationProperties
public class ServiceCasinoProviderIforiumApplication extends LithiumServiceApplication {

    @Autowired
    private ProgressiveJackpotFeedRegistrationService progressiveJackpotFeedRegistrationService;

    @Autowired
    private ModuleInfo moduleInfo;

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCasinoProviderIforiumApplication.class, args);
    }

    @Override
    public void startup(ApplicationStartedEvent e) throws Exception {
        super.startup(e);

        registerProgressiveJackpotFeeds();
    }

    private void registerProgressiveJackpotFeeds() {
        progressiveJackpotFeedRegistrationService.create(moduleInfo.getModuleName(),
                IForiumGameSuppliers.BLUEPRINT.getGameSupplierName());

        progressiveJackpotFeedRegistrationService.register();
    }
}
