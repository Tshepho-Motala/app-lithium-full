package lithium.service.access.jobs;

import lithium.leader.LeaderCandidate;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.access.config.Properties;
import lithium.service.access.data.entities.RawTransactionData;
import lithium.service.access.services.AccessRuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
@Slf4j
public class RecordsCleanUpJob {

  @Autowired
  private LeaderCandidate leaderCandidate;
  @Autowired
  private AccessRuleService accessRuleService;
  @Autowired
  private Properties properties;

  @TimeThisMethod
  @Scheduled(cron = "${lithium.service.access.jobs.remove-raw-data-records.cron:0 1 * * * *}")
  public void process() {
    log.debug("RecordsCleanUpJob Runnning");

    if (!leaderCandidate.iAmTheLeader()) {
      log.debug("I am not the leader.");
      return;
    }
    int page = 0;
    boolean hasMore = true;
    while (hasMore) {
      PageRequest pageRequest = PageRequest.of(page, properties.getRemoveRawDataRecords().getPageSize());

      Page<RawTransactionData> allPending = accessRuleService.findAllRawTransactionDataBefore(DateUtils.addDays(new Date(), properties.getRemoveRawDataRecords().getDays()), pageRequest);
      log.info("Found " + allPending.getContent().size() + " entries. Page " + allPending.getNumber() + " of " + allPending.getTotalPages());
      SW.start("RecordsCleanUpJob started");
      accessRuleService.deleteAllRawTransactionDataBefore(allPending.getContent());
      SW.stop();

      page++;
      if (!allPending.hasNext()) {
        hasMore = false;
      }

    }

  }
}
