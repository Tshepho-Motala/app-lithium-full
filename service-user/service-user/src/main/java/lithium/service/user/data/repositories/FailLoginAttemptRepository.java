package lithium.service.user.data.repositories;


import lithium.service.user.data.entities.FailLoginAttempt;
import org.joda.time.DateTime;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FailLoginAttemptRepository extends PagingAndSortingRepository<FailLoginAttempt, String> {
    @Transactional
    List<FailLoginAttempt> deleteAllByDomainNameAndDateAddedIsBefore(String domainName, DateTime date);

  default FailLoginAttempt findOne(String id) {
    return findById(id).orElse(null);
  }
}
