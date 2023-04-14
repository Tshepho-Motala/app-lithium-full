package lithium.service.leaderboard.data.projections;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import lithium.service.leaderboard.data.entities.Entry;

@Projection(name = "simpleEntry", types = { Entry.class })
public interface EntryProjection {
	@Value("#{target.id}")
	Long getId();
	Integer getRank();
	BigDecimal getScore();
	@Value("#{target.points.intValue()}")
	Integer getPoints();
	@Value("#{target.user.guid()}")
	String getUserGuid();
	@Value("#{target.leaderboardHistory.startDate}")
	Date getDateStart();
	@Value("#{target.leaderboardHistory.endDate}")
	Date getDateEnd();
	@Value("#{target.leaderboardHistory.leaderboard.scoreToPoints.intValue()}")
	Integer getScoreToPoints();
}