package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.RestrictionOutcomePlaceAction;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RestrictionOutcomePlaceActionRepository extends PagingAndSortingRepository<RestrictionOutcomePlaceAction, Long> {
	List<RestrictionOutcomePlaceAction> findBySet(DomainRestrictionSet set);
}
