package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.roxor.storage.entities.Game;
import org.springframework.cache.annotation.Cacheable;

public interface GameRepository extends FindOrCreateByGuidRepository<Game, Long> {

    @Cacheable(value = "lithium.service.casino.provider.roxor.storage.entities.game.byGuid", unless = "#result == null")
    Game findByGuid(String gameGuid);
}
