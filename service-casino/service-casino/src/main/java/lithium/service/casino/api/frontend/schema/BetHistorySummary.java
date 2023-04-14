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
public class BetHistorySummary {
	private Long totalBetCents;
	private Long totalBetCount;
	private Long totalWinCents;
	private Long totalWinCount;
	private Long totalProfitCents;
}
