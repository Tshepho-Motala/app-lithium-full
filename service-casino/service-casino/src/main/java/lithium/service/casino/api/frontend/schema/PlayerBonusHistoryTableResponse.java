package lithium.service.casino.api.frontend.schema;

import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.projection.entities.PlayerBonusFreespinHistoryProjection;
import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class PlayerBonusHistoryTableResponse {
	private Long id;
	private Date startedDate = new Date();
	private Long playThroughCents;
	private Long playThroughRequiredCents;
	private Long triggerAmount;
	private Long bonusAmount;
	private Integer bonusPercentage;
	private Boolean completed;
	private Boolean cancelled;
	private Boolean expired;
	private BonusRevision bonus;   //this should have been called bonusRevision..
	private PlayerBonus playerBonus;
	private Long customFreeMoneyAmountCents; // This is used in manual trigger for now
	private Long customBonusTokenAmountCents;
	private Long requestId;
	private String description;
	private String clientId;
	private Long sessionId;
	private PlayerBonusFreespinHistoryProjection playerBonusFreespinHistoryProjection;
}
