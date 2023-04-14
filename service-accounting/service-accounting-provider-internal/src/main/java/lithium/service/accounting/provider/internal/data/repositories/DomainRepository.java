package lithium.service.accounting.provider.internal.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.events.BalanceAdjustEvent;

public interface DomainRepository extends JpaRepository<Domain, Long> {
	
	@CacheEvict({
		"lithium.service.accounting.provider.internal.data.entities.Domain.byId",
		"lithium.service.accounting.provider.internal.data.entities.Domain.byCode",
	})
	@Override
	<S extends Domain> S save(S entity);

	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.Domain.byId", unless = "#result == null")
	default Domain findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.Domain.byCode", unless = "#result == null")
	Domain findByName(String name);

//	@TransactionalEventListener(phase=TransactionPhase.AFTER_ROLLBACK)
//	@CacheEvict(cacheNames={
//		"lithium.service.accounting.provider.internal.data.entities.Domain.byId",
//		"lithium.service.accounting.provider.internal.data.entities.Domain.byCode",
//	}, allEntries=true)
//	default void handleRollbackCacheEvict(BalanceAdjustEvent evt) {
//		//Body is just here so the compile and runtime proxies don't try to do something with this.
//	}
}
