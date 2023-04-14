package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.AutoWithdrawalRuleSetProcess;
import lithium.service.cashier.data.entities.Domain;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AutoWithdrawalRuleSetProcessRepository extends PagingAndSortingRepository<AutoWithdrawalRuleSetProcess, Long>, JpaSpecificationExecutor<AutoWithdrawalRuleSetProcess> {
	List<AutoWithdrawalRuleSetProcess> findByRulesetDomainAndStartedNotNullAndCompletedNull(Domain domain);
	AutoWithdrawalRuleSetProcess findTop1ByRulesetDomainAndStartedIsNull(Domain domain);
}
