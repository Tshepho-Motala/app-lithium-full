package lithium.service.user.search.data.repositories.user_search;

import java.util.List;
import lithium.service.user.search.data.entities.DomainRestriction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("user_search.DomainRestrictionsRepository")
public interface DomainRestrictionsRepository extends PagingAndSortingRepository<DomainRestriction, Long>, JpaSpecificationExecutor<DomainRestriction> {
  DomainRestriction findById(long id);
}
