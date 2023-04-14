package lithium.service.games.data.entities.progressivejackpotfeeds.projection.entities;

import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameSupplier;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "gameProjection", types = { Game.class })
public interface GameProjection {

    String getName();
    String getGuid();
    String getModuleSupplierId();

    // FIXME: refactor, remove.
    GameSupplier getGameSupplier();
}
