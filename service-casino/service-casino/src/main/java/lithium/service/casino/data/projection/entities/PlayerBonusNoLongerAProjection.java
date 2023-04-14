package lithium.service.casino.data.projection.entities;

import lithium.service.casino.data.entities.PlayerBonus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.rest.core.config.Projection;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class PlayerBonusNoLongerAProjection implements Serializable {
	private Long id;
	private String playerGuid;
	private PlayerBonusHistoryProjection current;
}
