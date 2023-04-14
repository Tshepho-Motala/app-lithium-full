package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.AutoRestrictionRule;
import lithium.service.limit.data.entities.AutoRestrictionRuleSet;
import lithium.service.limit.enums.AutoRestrictionRuleField;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AutoRestrictionRuleRepository extends PagingAndSortingRepository<AutoRestrictionRule, Long>,
		JpaSpecificationExecutor<AutoRestrictionRule> {
	AutoRestrictionRule findByRulesetAndField(AutoRestrictionRuleSet ruleset, AutoRestrictionRuleField field);
	default AutoRestrictionRule findOne(Long id) {
		return findById(id).orElse(null);
	}
}
