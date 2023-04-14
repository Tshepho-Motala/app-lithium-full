package lithium.service.migration;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.hazelcast.EnableHazelcastClient;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.access.client.EnableAccessService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@LithiumService
@EnableDomainClient
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableHazelcastClient
@EnableAccessService
@EntityScan(
    value = {
        "lithium.service.libraryvbmigration.data.entities",
        "lithium.service.migration.models.enities"
    }
)
@EnableJpaRepositories(basePackages = {"lithium.service.migration.repo"})
public class VbMigrationApplication extends LithiumServiceApplication {

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(VbMigrationApplication.class, args);
  }

}
