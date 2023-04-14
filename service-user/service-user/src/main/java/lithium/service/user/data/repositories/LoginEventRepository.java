package lithium.service.user.data.repositories;

import java.util.Date;
import java.util.List;
import lithium.service.user.data.entities.LoginEvent;
import lithium.service.user.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LoginEventRepository extends PagingAndSortingRepository<LoginEvent, Long>, JpaSpecificationExecutor<LoginEvent> {
    Long countByUserAndSuccessfulAndDateAfter(User user, boolean successful, Date date);
    LoginEvent findTop1ByUserGuidAndIdNotOrderByIdDesc(String userGuid, Long id);
    LoginEvent findBySessionKey(String sessionKey);
    Page<LoginEvent> findByDomainNameAndSuccessfulTrueAndLogoutIsNullAndLastActivityNotNullAndLastActivityBefore(String domainName,
        Date lastActivityBefore, Pageable pageable);
    Page<LoginEvent> findBySuccessfulTrueAndLogoutIsNullAndLastActivityIsNull(Pageable pageable);
    List<LoginEvent> findByUserAndSuccessfulTrueAndLogoutIsNull(User user);

    int countByUserAndSuccessfulTrueAndLogoutIsNull(User user);
    LoginEvent findTop1ByUserAndSuccessfulTrueAndLogoutIsNullOrderByDateAsc(User user);

  default LoginEvent findOne(Long id) {
    return findById(id).orElse(null);
  }
}
