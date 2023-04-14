package lithium.service.user.mass.action;

import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.casino.EnableSystemBonusClient;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.boot.SpringApplication;

@LithiumService
@EnableDomainClient
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableSystemBonusClient
@EnableUserApiInternalClientService
@EnableLimitInternalSystemClient
@EnableChangeLogService
public class ServiceUserMassActionApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUserMassActionApplication.class, args);
    }
}
