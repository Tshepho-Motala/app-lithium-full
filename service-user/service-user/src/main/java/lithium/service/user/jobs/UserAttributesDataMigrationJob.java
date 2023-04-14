package lithium.service.user.jobs;

import java.util.List;
import lithium.service.user.client.objects.UserAttributesData;
import lithium.service.user.client.stream.UserAttributesTriggerStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class UserAttributesDataMigrationJob {
  @Autowired
  private UserAttributesTriggerStream userRestrictionTriggerStream;
  @Autowired
  private DataMigrationService migrationService;

  @Async
  public void migrateUserAttributesData(boolean dryRun, int pageSize, Long delay) throws InterruptedException {
    log.info("/service-user/user-attributes, isMigrationJobStarted={}", JobState.isStarted);
    if (!JobState.isStarted) {
      JobState.start(dryRun);
      Pageable page = PageRequest.of(0, pageSize);
      long migratedAccountsCount;
      do {
        if (JobState.isTerminated) {
          break;
        }
        migratedAccountsCount = migrate(page);
        page = page.next();
        throttleMigration(delay);
      } while (migratedAccountsCount > 0);
      JobState.finish();
      log.info(":: Migration of users attributes is finished.");
    }
  }

  private long migrate(Pageable page) {
    List<UserAttributesData> attributesData = migrationService.getUserAttributesForSync(page);
    attributesData.stream()
        .forEach(data -> putToSyncStream(data));
    return attributesData.size();
  }

  private void putToSyncStream(UserAttributesData data) {
    try {
      if (!JobState.isDryRun) {
        userRestrictionTriggerStream.trigger(data);
      }
      log.debug("Migrated users attributes data : {}", data);
    } catch (Exception e) {
      log.error("Got error during users attributes migration (" + data + ")" + "\n:: Migration of this users attributes is rolled back...", e);
    }
  }

  private void throttleMigration(Long delay) throws InterruptedException {
    Thread.sleep(delay);
  }

  public void terminate() {
    JobState.terminate();
  }

  @Getter
  private static class JobState {

    private static boolean isStarted;
    private static boolean isTerminated;
    private static boolean isDryRun;

    public static void start(boolean dryRun) {
      isStarted = true;
      isTerminated = false;
      isDryRun = dryRun;
    }

    public static void terminate() {
      isTerminated = true;
      isStarted = false;
      log.info(":: Migration of users attributes was terminated");
    }

    private static void finish() {
      isStarted = false;
    }
  }
}
