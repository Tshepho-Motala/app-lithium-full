package lithium.service.accounting.provider.internal.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lithium.service.accounting.provider.internal.data.entities.Label;
import lithium.service.accounting.provider.internal.events.BalanceAdjustEvent;

public interface LabelRepository extends PagingAndSortingRepository<Label, Long> {

	@CacheEvict({
		"lithium.service.accounting.provider.internal.data.entities.Label.byId",
		"lithium.service.accounting.provider.internal.data.entities.Label.byName",
	})
	@Override
	<S extends Label> S save(S entity);

	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.Label.byId", unless = "#result == null")
	default Label findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.Label.byName", unless = "#result == null")
	Label findByName(String name);

//	@TransactionalEventListener(phase=TransactionPhase.AFTER_ROLLBACK)
//	@CacheEvict(cacheNames = {
//		"lithium.service.accounting.provider.internal.data.entities.Label.byId",
//		"lithium.service.accounting.provider.internal.data.entities.Label.byName",
//	}, allEntries=true)
//	default void handleRollbackCacheEvict(BalanceAdjustEvent event) {
//		//Body is just here so the compile and runtime proxies don't try to do something with this.
//	}
}
