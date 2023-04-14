package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.PlayerCoolOff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

public interface PlayerCoolOffRepository extends PagingAndSortingRepository<PlayerCoolOff, Long>, JpaSpecificationExecutor<PlayerCoolOff> {
// PLAT-3718 - @Cacheable(cacheNames="lithium.service.limit.playercooloff", key="#root.args[0]", unless="#result == null")
// Removed to assist with long-running queries.
// The assumption is that we can fetch this data much faster from the DB directly.
// Table has under 1000 records, database can do this much faster, no need for cache
	public PlayerCoolOff findByPlayerGuid(String playerGuid);

	@Transactional
	@Modifying
//	PLAT-3718 - @CacheEvict(cacheNames="lithium.service.limit.playercooloff", key="#root.args[0]")
	public void deleteByPlayerGuid(String playerGuid);

	@Override
//	PLAT-3718 - @CacheEvict(cacheNames="lithium.service.limit.playercooloff", key="#result.playerGuid")
	@Transactional
	<S extends PlayerCoolOff> S save(S arg0);

	Page<PlayerCoolOff> findByExpiryDateBeforeOrderByExpiryDate(Date expiryDateBefore, Pageable pageRequest);
}
