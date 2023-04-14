package lithium.service.casino.data.projection.entities;

import org.springframework.data.rest.core.config.Projection;

import lithium.service.casino.data.entities.PlayerBonusFreespinHistory;

@Projection(name = "playerBonusFreespinHistoryProjection", types = { PlayerBonusFreespinHistory.class })
public interface PlayerBonusFreespinHistoryProjection {
	Long getId();
	
	BonusRulesFreespinsProjection getBonusRulesFreespins();
	
	Integer getFreespinsRemaining();
	
	Integer getExtBonusId();
}
