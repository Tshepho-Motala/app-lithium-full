package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.PlayerExclusionV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface PlayerExclusionV2Repository extends PagingAndSortingRepository<PlayerExclusionV2, Long> {
	
//	@Cacheable(cacheNames="lithium.service.limit.playerexclusionv2", key="#root.args[0]", unless="#result == null")
	PlayerExclusionV2 findByPlayerGuid(String playerGuid);
	
	
	@Query("select o from #{#entityName} o where o.playerGuid = :playerGuid and (o.expiryDate is NULL or o.expiryDate > :expiryDateGreaterThan)")
	PlayerExclusionV2 findByPlayerGuidAndExpiryDateIsNullOrExpiryDateGreaterThan(@Param("playerGuid") String playerGuid, @Param("expiryDateGreaterThan") Date expiryDateGreaterThan);
	
	@Modifying
	@Transactional
//	@CacheEvict(cacheNames="lithium.service.limit.playerexclusionv2", key="#root.args[0]")
	void deleteByPlayerGuid(String playerGuid);
	
	@Override
//	@CacheEvict(cacheNames="lithium.service.limit.playerexclusionv2", key="#result.playerGuid")
	@Transactional(rollbackFor = Exception.class)
	<S extends PlayerExclusionV2> S save(S arg0);

	Page<PlayerExclusionV2> findByExpiryDateNotNullAndExpiryDateBeforeOrderByExpiryDate(Date expiryDateBefore, Pageable pageRequest);
}
