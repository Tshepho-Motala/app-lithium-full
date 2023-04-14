package lithium.service.stats.objects;

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
public class DashboardStatSummary {
	private Long countcurrent;
	private Long countlast1;
	private Long countlast2;
	private Long counttotal;
}
