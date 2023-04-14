package lithium.service.leaderboard.data.projections;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.fasterxml.jackson.annotation.JsonProperty;

import lithium.service.leaderboard.data.entities.Leaderboard;

@Projection(name = "simpleLeaderboard", types = { Leaderboard.class })
public interface LeaderboardProjection {
	Long getId();
	String getName();
	String getDescription();
	Boolean getVisible();
	Boolean getEnabled();
	@JsonProperty("top")
	Integer getAmount();
	Integer getXpLevelMin();
	Integer getXpLevelMax();
	Long getXpPointsMin();
	Long getXpPointsMax();
	Integer getXpPointsPeriod();
	@Value("#{target.xpPointsGranularity.type()}")
	String getXpPointsGranularity();
	BigDecimal getScoreToPoints();
	
	Integer getDurationPeriod();
	@Value("#{target.durationGranularity.type()}")
	String getDurationGranularity();
}