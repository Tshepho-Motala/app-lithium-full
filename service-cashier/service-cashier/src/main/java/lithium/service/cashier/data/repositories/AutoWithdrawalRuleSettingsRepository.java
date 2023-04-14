package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSettings;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AutoWithdrawalRuleSettingsRepository extends PagingAndSortingRepository<AutoWithdrawalRuleSettings, Long>,
	JpaSpecificationExecutor<AutoWithdrawalRuleSettings> {
	List<AutoWithdrawalRuleSettings> findByRule(AutoWithdrawalRule rule);
}
