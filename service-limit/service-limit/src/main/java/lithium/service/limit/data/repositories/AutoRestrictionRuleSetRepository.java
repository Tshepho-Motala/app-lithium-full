package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.AutoRestrictionRuleSet;
import lithium.service.limit.data.entities.Domain;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AutoRestrictionRuleSetRepository extends PagingAndSortingRepository<AutoRestrictionRuleSet, Long>,
		JpaSpecificationExecutor<AutoRestrictionRuleSet> {
	AutoRestrictionRuleSet findByDomainAndName(Domain domain, String name);

	default AutoRestrictionRuleSet findOne(Long id) {
		return findById(id).orElse(null);
	}
}
