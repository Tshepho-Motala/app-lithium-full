package lithium.service.games.data.repositories;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.games.data.entities.GameLabelValue;

public interface GameLabelValueRepository extends PagingAndSortingRepository<GameLabelValue, Long> {
	
	@Cacheable(key="#root.args[0]", cacheNames="lithium.service.games.data.repositories.findByGameId", unless="#result == null")
	List<GameLabelValue> findByGameId(Long gameId);
	
	@Cacheable(key="#root.args[0]", cacheNames="lithium.service.games.data.repositories.findByGameIdAndDeletedFalse", unless="#result == null")
	List<GameLabelValue> findByGameIdAndDeletedFalse(Long gameId);
	
//	@Cacheable(key="#root.args[0],#root.args[1]", cacheNames="lithium.service.games.data.repositories.findByGameIdAndLabelValueId", unless="#result == null")
	GameLabelValue findByGameIdAndLabelValueId(Long gameId, Long labelValueId);
	
	GameLabelValue findByGameIdAndLabelValueLabelNameAndDeletedFalse(Long gameId, String labelName);
	
//	GameLabelValue findByGameIdAndLabelValueIdAndDeletedFalse(Long gameId, Long labelValueId);
	
	@Override
	@Caching(evict = {
		@CacheEvict(cacheNames={"lithium.service.games.data.repositories.findByGameIdAndDeletedFalse",
							"lithium.service.games.data.repositories.findByGameId"}, key="#root.args[0].getGameId()"),
		@CacheEvict(cacheNames={"lithium.service.games.services.getEffectiveLabels"}, allEntries = true)
		})
	public <S extends GameLabelValue> S save(S entity);
}
