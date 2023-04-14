package lithium.service.accounting.provider.internal.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.events.BalanceAdjustEvent;

@Repository
public interface AccountCodeRepository extends PagingAndSortingRepository<AccountCode, Long> {
	
	@CacheEvict({
		"lithium.service.accounting.provider.internal.data.entities.AccountCode.byCode", 
		"lithium.service.accounting.provider.internal.data.entities.AccountCode.byId",
	})
	@Override
	<S extends AccountCode> S save(S entity);

	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.AccountCode.byId", unless = "#result == null")
	default AccountCode findOne(Long id) {
		return findById(id).orElse(null);
	}
	
	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.AccountCode.byCode", unless = "#result == null")
	AccountCode findByCode(String code);
	
//	@TransactionalEventListener(phase=TransactionPhase.AFTER_ROLLBACK)
//	@Caching(evict= { @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.AccountCode.byId"}, condition="#root.args[0].getTranEntry() != null", key="#root.args[0].getTranEntry().getAccount().getAccountCode().getId()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.AccountCode.byCode"}, condition="#root.args[0].getTranEntry() != null", key="#root.args[0].getTranEntry().getAccount().getAccountCode().getCode()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.AccountCode.byId"}, condition="#root.args[0].getTranContraEntry() != null", key="#root.args[0].getTranContraEntry().getAccount().getAccountCode().getId()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.AccountCode.byCode"}, condition="#root.args[0].getTranContraEntry() != null", key="#root.args[0].getTranContraEntry().getAccount().getAccountCode().getCode()")
//	})
//	default void handleRollbackCacheEvict(BalanceAdjustEvent event) {
//		//Body is just here so the compile and runtime proxies don't try to do something with this.
//	}
	
}
