package lithium.service.user.services;

import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.objects.UserJobRequest;
import lithium.service.user.data.entities.UserJob;
import lithium.service.user.enums.JobStatus;
import lithium.service.user.data.repositories.DomainRepository;
import lithium.service.user.data.repositories.UserJobRepository;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Date;

@Service
public class UserJobService {
  @Autowired
  UserJobRepository userJobRepository;

  @Autowired
  DomainRepository domainRepository;

  public UserJob findJobByStatus(JobStatus jobStatus) {
    return userJobRepository.findFirstByStatusOrderByCreatedDateDesc(jobStatus.status());
  }

  public UserJob findLastByStatusAndCreatedAfter(JobStatus status, DateTime after) {
    return userJobRepository.findFirstByStatusAndCreatedDateAfterOrderByCreatedDateDesc(status.status(), after);
  }

  public UserJob findLastCreatedOrFailedJob() {
    return userJobRepository.findFirstByStatusInOrderByCreatedDateDesc(Arrays.asList(JobStatus.CREATED.status(), JobStatus.FAILED.status()));
  }

  public UserJob findOrCreate(UserJobRequest userJobRequest, long userId) throws Exception {

    Domain domain = domainRepository.findByName(userJobRequest.getDomain());

    if(domain == null) {
      throw new Exception("An invalid domain was provided");
    }

      UserJob userJob = userJobRepository.findFirstByDomainNameAndStatusOrderByCreatedDateDesc(userJobRequest.getDomain(), JobStatus.CREATED
        .status());


    if(userJob == null) {
        userJob = UserJob.builder()
            .status(JobStatus.CREATED.status())
            .pageSize(userJobRequest.getPageSize())
            .phoneLength(userJobRequest.getPhoneLength())
            .createdDate(DateTime.now(DateTimeZone.UTC))
            .domain(domain)
            .user(User.builder().id(userId).build())
            .build();
        userJobRepository.save(userJob);
    }

    return userJob;
  }
}
