package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.AutoWithdrawalRuleSet;
import lithium.service.cashier.data.entities.Domain;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AutoWithdrawalRuleSetRepository extends PagingAndSortingRepository<AutoWithdrawalRuleSet, Long>,
		JpaSpecificationExecutor<AutoWithdrawalRuleSet> {
	AutoWithdrawalRuleSet findByDomainAndName(Domain domain, String name);
	List<AutoWithdrawalRuleSet> findByDomainAndEnabledTrueAndDelayedStartTrue(Domain domain);
//

	default AutoWithdrawalRuleSet findOne(Long id) {
		return findById(id).orElse(null);
	}
}
