package lithium.service.casino.data.projection.entities;

import lithium.service.casino.data.entities.PlayerBonusPending;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "PlayerBonusPendingProjection", types = { PlayerBonusPending.class })
public interface PlayerBonusPendingProjection {

	Long getId();

	Date getCreatedDate();

	Long getPlayThroughRequiredCents();

	Long getTriggerAmount();

	Long getBonusAmount();

	Integer getBonusPercentage();

	BonusRevisionProjection getBonusRevision();

	String getPlayerGuid();

	Long getCustomFreeMoneyAmountCents();
}
