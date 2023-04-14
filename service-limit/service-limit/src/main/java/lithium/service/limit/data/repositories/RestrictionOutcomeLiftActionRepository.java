package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.RestrictionOutcomeLiftAction;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RestrictionOutcomeLiftActionRepository extends PagingAndSortingRepository<RestrictionOutcomeLiftAction, Long> {
	List<RestrictionOutcomeLiftAction> findBySet(DomainRestrictionSet set);
}
