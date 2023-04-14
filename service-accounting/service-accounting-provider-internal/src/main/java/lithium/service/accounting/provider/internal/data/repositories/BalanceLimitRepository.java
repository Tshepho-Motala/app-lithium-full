package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.LockingPagingSortingRepository;
import lithium.service.accounting.provider.internal.data.entities.BalanceLimit;

public interface BalanceLimitRepository extends LockingPagingSortingRepository<BalanceLimit, Long> {
	BalanceLimit findByAccountOwnerGuidAndAccountAccountCodeCodeAndAccountAccountTypeCode(String playerGuid, String accountCode, String accountType);
}
