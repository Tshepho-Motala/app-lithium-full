package lithium.service.leaderboard.client.objects;

import java.math.BigDecimal;

import org.joda.time.DateTime;

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
public class LeaderboardBasic {
	private String name;
	private String description;
	private String domainName;
	private Integer xpLevelMin;
	private Integer xpLevelMax;
	private BigDecimal xpPointsMin;
	private BigDecimal xpPointsMax;
	private Integer xpPointsPeriod;
	private Granularity xpPointsGranularity;
	
	private DateTime startDate;
	
	private Integer durationPeriod;
	private Granularity durationGranularity;
	private String recurrencePattern;
	
	private Integer amount;
	private BigDecimal scoreToPoints;
	private String notification;
	private String notificationNonTop;
}