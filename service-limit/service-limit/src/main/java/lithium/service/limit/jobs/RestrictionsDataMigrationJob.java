package lithium.service.limit.jobs;

import java.util.ArrayList;
import java.util.List;
import lithium.service.limit.client.stream.UserRestrictionTriggerStream;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.UserRestrictionSet;
import lithium.service.limit.data.repositories.UserRestrictionSetRepository;
import lithium.service.user.client.objects.RestrictionData;
import lithium.service.user.client.objects.RestrictionsMessageType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RestrictionsDataMigrationJob {

  @Autowired
  private UserRestrictionSetRepository userRestrictionSetRepository;

  @Autowired
  private UserRestrictionTriggerStream userRestrictionTriggerStream;

  @Async
  public void migratePlayerRestrictionsData(boolean dryRun, int pageSize, Long delay) throws InterruptedException {
    log.info("/service-limit/user-restrictions, isMigrationJobStarted={}", JobState.isStarted);
    if (!JobState.isStarted) {
      JobState.start(dryRun);
      Pageable page = PageRequest.of(0, pageSize);
      long migratedAccountsCount;
      do {
        if (JobState.isTerminated) break;
        migratedAccountsCount = migrate(page);
        page = page.next();
        throttleMigration(delay);
      } while (migratedAccountsCount > 0);
      JobState.finish();
      log.info(":: Migration of users restrictions is finished.");
    }
  }

  private long migrate(Pageable page) {
    return fromExternalStorage(page).stream().peek(this::toLocalStorage).count();
  }

  private List<RestrictionData> fromExternalStorage(Pageable page) {
    List<UserRestrictionSet> userRestrictionSets = userRestrictionSetRepository.findAllBySetDeletedFalse(page);
    List<RestrictionData> restrictionDataList = new ArrayList<>();
    userRestrictionSets.stream().forEach(set -> {
      DomainRestrictionSet domainSet = set.getSet();
      RestrictionData restrictionData = RestrictionData.builder()
          .domainRestrictionId(domainSet.getId())
          .domainRestrictionName(domainSet.getName())
          .guid(set.getUser().getGuid())
          .domainName(domainSet.getDomain().getName())
          .enabled(domainSet.isEnabled())
          .deleted(domainSet.isDeleted())
          .activeFrom(set.getActiveFrom())
          .activeTo(set.getActiveTo())
          .messageType(RestrictionsMessageType.USER_SET_UPDATE)
          .build();
      restrictionDataList.add(restrictionData);
    });
    return restrictionDataList;
  }

  private void toLocalStorage(RestrictionData data) {
    try {
      if (!JobState.isDryRun) userRestrictionTriggerStream.trigger(data);
      log.info("Migrated restriction : {}", data);
    } catch (Exception e) {
      log.error("Got error during restriction migration (" + data + ")" + "\n:: Migration of this restriction is rolled back...", e);
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
    }

    private static void finish() {
      isStarted = false;
    }
  }
}
