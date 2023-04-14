package lithium.service.games.data.entities.progressivejackpotfeeds.projection.repository;

import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameSupplier;
import lithium.service.games.data.entities.progressivejackpotfeeds.projection.entities.GameProjection;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GameProjectionRepository extends PagingAndSortingRepository<Game, Long> {
    List<GameProjection> findByGameSupplierAndEnabledAndProgressiveJackpot(GameSupplier gameSupplier, Boolean enabled,
            Boolean progressiveJackpot);
}
