package lithium.service.accounting.provider.internal.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.events.BalanceAdjustEvent;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

//	@CacheEvict({
//		"lithium.service.accounting.provider.internal.data.entities.User.byId",
//		"lithium.service.accounting.provider.internal.data.entities.User.byGuid",
//	})
//	@Override
//	<S extends User> S save(S entity);

//	@Override
//	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.User.byId", unless = "#result == null")
//	User findOne(Long id);

//	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.User.byGuid", unless = "#result == null")
	User findByGuid(String guid);

//	@TransactionalEventListener(phase=TransactionPhase.AFTER_ROLLBACK)
//	@Caching(evict= { @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.User.byId"},condition="#root.args[0].getAuthor() != null", key="#root.args[0].getAuthor().getId()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.User.byGuid"}, condition="#root.args[0].getAuthor() != null", key="#root.args[0].getAuthor().getGuid()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.User.byId"}, condition="#root.args[0].getTranEntry() != null", key="#root.args[0].getTranEntry().getAccount().getOwner().getId()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.User.byGuid"}, condition="#root.args[0].getTranEntry() != null", key="#root.args[0].getTranEntry().getAccount().getOwner().getGuid()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.User.byId"}, condition="#root.args[0].getTranContraEntry() != null", key="#root.args[0].getTranContraEntry().getAccount().getOwner().getId()"),
//					  @CacheEvict(cacheNames={"lithium.service.accounting.provider.internal.data.entities.User.byGuid"}, condition="#root.args[0].getTranContraEntry() != null", key="#root.args[0].getTranContraEntry().getAccount().getOwner().getGuid()")
//	})
//	default void handleRollbackCacheEvict(BalanceAdjustEvent event) {
//		//Body is just here so the compile and runtime proxies don't try to do something with this.
//	}
}
