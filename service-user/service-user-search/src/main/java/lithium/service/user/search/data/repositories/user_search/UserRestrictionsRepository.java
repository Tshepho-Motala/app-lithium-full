package lithium.service.user.search.data.repositories.user_search;

import lithium.service.user.search.data.entities.DomainRestriction;
import lithium.service.user.search.data.entities.UserRestriction;
import lithium.service.user.search.data.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("user_search.UserRestrictionsRepository")
public interface UserRestrictionsRepository extends PagingAndSortingRepository<UserRestriction, Long>, JpaSpecificationExecutor<UserRestriction> {
  void delete(UserRestriction userRestriction);
  UserRestriction findByUserAndDomainRestriction(User user, DomainRestriction domainRestriction);
}
