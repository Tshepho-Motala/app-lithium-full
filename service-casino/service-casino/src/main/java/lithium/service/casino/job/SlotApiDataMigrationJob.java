package lithium.service.casino.job;

import lithium.leader.LeaderCandidate;
import lithium.service.casino.config.ServiceCasinoConfigurationProperties;
import lithium.service.casino.service.SlotApiDataMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "lithium.services.casino.slot-api-data-migration-job.enabled")
@Slf4j
public class SlotApiDataMigrationJob {
  @Autowired private LeaderCandidate leaderCandidate;
  @Autowired private ServiceCasinoConfigurationProperties properties;
  @Autowired private SlotApiDataMigrationService service;

  @Scheduled(fixedDelayString = "${lithium.services.casino.slot-api-data-migration-job.delay-ms:60000}")
  public void process() {
    if (!leaderCandidate.iAmTheLeader()) {
      log.trace("I am not the leader.");
      return;
    }

    service.migrate();
  }
}
