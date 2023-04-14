package lithium.service.casino.data.projection.entities;

import org.springframework.data.rest.core.config.Projection;

import lithium.service.casino.data.entities.BonusRulesFreespins;

@Projection(name = "bonusRulesFreespinsProjection", types = { BonusRulesFreespins.class })
public interface BonusRulesFreespinsProjection {
	Long getId();
	
	String getProvider();
	
	Integer getFreespins();
	
	Integer getWagerRequirements();
}