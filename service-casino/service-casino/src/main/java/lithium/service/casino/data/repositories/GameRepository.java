package lithium.service.casino.data.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.data.entities.Game;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends FindOrCreateByGuidRepository<Game, Long> {

	@Cacheable(value = "lithium.service.casino.entities.game.byGuid", unless = "#result == null")
	Game findByGuid(String gameGuid);

	Iterable<Game> findAllByGuidIn(List<String> guids);
}
