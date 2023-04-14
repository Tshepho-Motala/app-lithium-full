package lithium.service.casino.provider.slotapi.storage.repositories;

import java.util.List;
import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.slotapi.storage.entities.Game;
import org.springframework.cache.annotation.Cacheable;

/**
 *
 */
public interface GameRepository extends FindOrCreateByGuidRepository<Game, Long> {

	@Cacheable(value = "lithium.service.casino.provider.slotapi.storage.entities.game.byGuid", unless = "#result == null")
	Game findByGuid(String gameGuid);

	Iterable<Game> findAllByGuidIn(List<String> guids);
}
