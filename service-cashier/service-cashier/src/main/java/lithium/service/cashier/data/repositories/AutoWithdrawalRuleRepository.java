package lithium.service.cashier.data.repositories;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSet;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AutoWithdrawalRuleRepository extends PagingAndSortingRepository<AutoWithdrawalRule, Long>,
		JpaSpecificationExecutor<AutoWithdrawalRule> {
	AutoWithdrawalRule findByRulesetAndField(AutoWithdrawalRuleSet ruleset, AutoWithdrawalRuleType field);
}
