package lithium.service.leaderboard.client.objects;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardConversionBasic implements Serializable {
	private static final long serialVersionUID = -1687363987778693525L;

	private Long id;
	
	private Long leaderboardId;
	
	private Integer typeId;
	
	private BigDecimal conversion;
}