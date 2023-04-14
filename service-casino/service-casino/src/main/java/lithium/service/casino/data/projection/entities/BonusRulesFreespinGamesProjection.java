package lithium.service.casino.data.projection.entities;

import org.springframework.data.rest.core.config.Projection;

import lithium.service.casino.data.entities.BonusRulesFreespinGames;

@Projection(name = "bonusRulesFreespinGamesProjection", types = { BonusRulesFreespinGames.class })
public interface BonusRulesFreespinGamesProjection {
//	private Long id;
	
	String getGameId();
	
//	private BonusRulesFreespins bonusRulesFreespins;
}