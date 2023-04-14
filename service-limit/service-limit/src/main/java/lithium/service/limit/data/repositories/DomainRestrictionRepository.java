package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.DomainRestriction;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainRestrictionRepository extends PagingAndSortingRepository<DomainRestriction, Long>, JpaSpecificationExecutor<DomainRestriction> {
	DomainRestriction findBySetAndRestrictionCode(DomainRestrictionSet set, String restrictionCode);
	default DomainRestriction findOne(Long id) {
		return findById(id).orElse(null);
	}
}
