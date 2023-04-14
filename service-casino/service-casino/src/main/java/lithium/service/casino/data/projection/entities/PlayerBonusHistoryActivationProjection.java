package lithium.service.casino.data.projection.entities;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.entities.PlayerBonusHistory;

@Projection(name = "playerBonusHistoryActivationProjection", types = { PlayerBonusHistory.class })
public interface PlayerBonusHistoryActivationProjection {
	//TODO: Remove once spring-data-releasetrain.version has been upgraded to : 
	// 1.11.5 (Gosling SR5), 1.12.3 (Hopper SR3), 1.13 RC1 (Ingalls) - Or above.
	//	@JsonIgnore
	//	Class<?> getDecoratedClass();
	Long getId();
	Date getStartedDate();
	Long getPlayThroughCents();
	Long getPlayThroughRequiredCents();
	Long getTriggerAmount();
	
	Long getBonusAmount();
	Integer getBonusPercentage();
	
	Boolean getCompleted();
	Boolean getCancelled();
	Boolean getExpired();
	
	BonusRevisionProjection getBonus();
	PlayerBonus getPlayerBonus();
}
