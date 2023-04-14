package lithium.service.casino.data.objects;

import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.projection.entities.PlayerBonusFreespinHistoryProjection;
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
public class PlayerBonusHistoryDisplay {
	private PlayerBonusHistory playerBonusHistory;
	private PlayerBonusFreespinHistoryProjection playerBonusFreespinHistoryProjection;
}