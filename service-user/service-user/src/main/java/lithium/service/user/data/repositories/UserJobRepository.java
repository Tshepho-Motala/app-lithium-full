package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.UserJob;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface UserJobRepository extends JpaRepository<UserJob, Long> {
  UserJob findFirstByDomainNameAndStatusOrderByCreatedDateDesc(String domainName, int status);
  UserJob findFirstByStatusOrderByCreatedDateDesc(int status);
  UserJob findFirstByStatusAndCreatedDateAfterOrderByCreatedDateDesc(int status, DateTime afterDate);
  UserJob findFirstByStatusInOrderByCreatedDateDesc(List<Integer> statuses);
}
