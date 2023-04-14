package lithium.service.user.jobs;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.leader.LeaderCandidate;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserJob;
import lithium.service.user.data.specifications.UserSpecifications;
import lithium.service.user.enums.JobStatus;
import lithium.service.user.data.repositories.UserJobRepository;
import lithium.service.user.data.repositories.UserRepository;
import lithium.service.user.services.UserJobService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;


@Component
@Slf4j
public class RemoveLeadingZeroJob {

  @Autowired
  private LeaderCandidate leaderCandidate;

  @Autowired
  private UserJobService userJobService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserJobRepository userJobRepository;

  @Autowired
  ChangeLogService changeLogService;

  @Scheduled(cron = "${lithium.services.user.remove-phone-leading-zero-job.cron}")
  public void process() throws Exception {
    log.debug("RemoveLeadingZeroJob is now running");
    if(!leaderCandidate.iAmTheLeader()) {
      log.debug("I am not the leader.");
      return;
    }

    DateTime after = DateTime.now(DateTimeZone.UTC).minusMinutes(60);
    //Select a processing job that has been created 1 hour ago and still processing
    UserJob userJob = userJobService.findLastByStatusAndCreatedAfter(JobStatus.PROCESSING, after);

    if(userJob != null) {
      log.debug(String.format("Job %s is busy processing", userJob.getId()));
      return;
    }

    userJob = userJobService.findLastCreatedOrFailedJob();

    log.debug("Retrieved Leading Zero Job", userJob);

    if(userJob == null) {
      log.debug("No leading zero jobs to run");
      return;
    }

    try {

      userJob.setStatus(JobStatus.PROCESSING.status());
      userJobRepository.save(userJob);

      log.debug(String.format("Job status for Job %s changed to %s", userJob.getId(), JobStatus.PROCESSING.name()));

      Page<User> results = null;
      Pageable pageRequest =   PageRequest.of(0,userJob.getPageSize());

      do
      {
        log.debug("Fetching users with leading zero cellphone numbers", pageRequest);

        Specification<User> spec = Specification.where(UserSpecifications.domainIn(Arrays.asList(userJob.getDomain())));
        spec = spec.and(UserSpecifications.cellphoneNumberWithLeadingZero(userJob.getPhoneLength()));

       // results = userRepository.findAllByDomainNameAndCellphoneNumberLength(pageRequest, userJob.getDomain().getName(), userJob.getPhoneLength(), "0%");

        results = userRepository.findAll(spec, pageRequest);

        log.debug(String.format("Found %s users from the db", results.getTotalElements()));

        for(User user: results.getContent()) {
          log.debug(String.format("Processing user %s", user.getGuid()));
          String oldCellphoneNumber = user.getCellphoneNumber();
          String newCellphoneNumber = oldCellphoneNumber.replaceFirst("0", "");

          if(!oldCellphoneNumber.equals(newCellphoneNumber)) {

            user.setCellphoneNumber(newCellphoneNumber);
            userRepository.save(user);

            List<ChangeLogFieldChange> clfc = Arrays.asList(
                ChangeLogFieldChange.builder()
                    .field("cellphoneNumber")
                    .fromValue(oldCellphoneNumber)
                    .toValue(newCellphoneNumber)
                    .build()
            );
            changeLogService.registerChangesWithDomain("user", "edit", user.getId(), userJob.getUser().guid(), "User cellphone number was updated",null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS,0,
                user.getDomain().getName());

            log.debug(String.format("Changing cellphone number for user %s", user.getGuid()));

            Thread.sleep(100);
          }

        }
      }
      while(!results.isLast());

      userJob.setCompletedDate(DateTime.now());
      userJob.setStatus(JobStatus.COMPLETE.status());
      userJobRepository.save(userJob);

      log.debug(String.format("Job status for Job %s changed to %s", userJob.getId(), JobStatus.COMPLETE.name()));
    }
    catch (Exception e) {
      userJob.setStatus(JobStatus.FAILED.status());
      userJobRepository.save(userJob);

      log.error("Failed while processing," + e.getMessage(), e);
    }

  }
}
