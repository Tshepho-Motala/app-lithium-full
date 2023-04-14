package lithium.service.leaderboard.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryBasic {
	private Long id;
	private Integer rank;
	private BigDecimal score;
	private Integer points;
	private String userGuid;
	private Date dateStart;
	private Date dateEnd;
	private Integer scoreToPoints;
	private String userName;
	private String firstName;
	private String prizeDescription;
	private String prizeImageBase64; // TODO FIXME TODO: this is a temporary solution
}
