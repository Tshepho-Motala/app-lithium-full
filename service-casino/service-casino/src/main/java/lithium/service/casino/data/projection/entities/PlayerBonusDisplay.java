package lithium.service.casino.data.projection.entities;

import java.util.List;

import lithium.service.casino.data.entities.PlayerBonusExternalGameLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerBonusDisplay {
	private PlayerBonusNoLongerAProjection playerBonusProjection;
	private PlayerBonusFreespinHistoryProjection playerBonusFreespinHistoryProjection;
	private List<BonusRulesFreespinGamesProjection> bonusRulesFreespinGamesProjection;
	private List<String> playerBonusExternalGameLinks;
}
