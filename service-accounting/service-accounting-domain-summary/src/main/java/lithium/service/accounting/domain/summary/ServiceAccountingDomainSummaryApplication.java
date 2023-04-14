package lithium.service.accounting.domain.summary;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.accounting.domain.summary.config.Properties;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lithium.shards.EnableShardsRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableShardsRegistry
@EnableScheduling
@EnableConfigurationProperties(Properties.class)
public class ServiceAccountingDomainSummaryApplication extends LithiumServiceApplication {
    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceAccountingDomainSummaryApplication.class, args);
    }
}
