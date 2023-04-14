package lithium.service.accounting.provider.internal.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lithium.service.accounting.provider.internal.data.entities.AccountType;
import lithium.service.accounting.provider.internal.events.BalanceAdjustEvent;

public interface AccountTypeRepository extends PagingAndSortingRepository<AccountType, Long> {

	@CacheEvict({
			"lithium.service.accounting.provider.internal.data.entities.AccountType.byId",
			"lithium.service.accounting.provider.internal.data.entities.AccountType.byCode",
	})
	@Override
	<S extends AccountType> S save(S entity);

	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.AccountType.byId", unless = "#result == null")
	default AccountType findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.AccountType.byCode", unless = "#result == null")
	AccountType findByCode(String code);

//	@TransactionalEventListener(phase=TransactionPhase.AFTER_ROLLBACK)
//	@Caching(evict= { @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.AccountType.byId"}, condition="#root.args[0].getTranEntry() != null", key="#root.args[0].getTranEntry().getAccount().getAccountType().getId()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.AccountType.byCode"},condition="#root.args[0].getTranEntry() != null", key="#root.args[0].getTranEntry().getAccount().getAccountType().getCode()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.AccountType.byId"}, condition="#root.args[0].getTranContraEntry() != null", key="#root.args[0].getTranContraEntry().getAccount().getAccountType().getId()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.AccountType.byCode"}, condition="#root.args[0].getTranContraEntry() != null", key="#root.args[0].getTranContraEntry().getAccount().getAccountType().getCode()")
//	})
//	default void handleRollbackCacheEvict(BalanceAdjustEvent event) {
//		//Body is just here so the compile and runtime proxies don't try to do something with this.
//	}
}
