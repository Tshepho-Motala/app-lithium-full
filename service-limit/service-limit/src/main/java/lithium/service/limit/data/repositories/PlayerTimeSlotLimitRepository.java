package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.PlayerTimeSlotLimit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;

public interface PlayerTimeSlotLimitRepository extends PagingAndSortingRepository<PlayerTimeSlotLimit, Long> {
//    @Cacheable(cacheNames="lithium.service.limit.playertime", key="#root.args[0]", unless="#result == null")
    PlayerTimeSlotLimit findByPlayerGuid(String playerGuid);

    @Override
//    @CacheEvict(cacheNames="lithium.service.limit.playertime", key="#result.getPlayerGuid()")
    <S extends PlayerTimeSlotLimit> S save(S arg0);

    @Transactional
    @Modifying
//    @CacheEvict(cacheNames="lithium.service.limit.playertime", key="#root.args[0]")
    void deleteByPlayerGuid(String playerGuid);
}
