package lithium.service.casino.api.frontend.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SessionRecap {
	private Long totalStakeCents;
	private Long totalWinCents;
	private Long totalLossCents;
}
