package lithium.service.casino.data.projection.entities;

import org.springframework.data.rest.core.config.Projection;

import lithium.service.casino.data.entities.PlayerBonus;

@Projection(name = "playerBonusProjection", types = { PlayerBonus.class })
public interface PlayerBonusProjection {
	Long getId();
	//TODO: Remove once spring-data-releasetrain.version has been upgraded to : 
	// 1.11.5 (Gosling SR5), 1.12.3 (Hopper SR3), 1.13 RC1 (Ingalls) - Or above.
	//@JsonIgnore
	//Class<?> getDecoratedClass();
	String getPlayerGuid();
	PlayerBonusHistoryProjection getCurrent();
}
