package lithium.service.leaderboard.data.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.leaderboard.client.objects.Granularity;
import lithium.service.leaderboard.data.entities.Domain;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.projections.LeaderboardProjection;

public interface LeaderboardRepository extends PagingAndSortingRepository<Leaderboard, Long>, JpaSpecificationExecutor<Leaderboard> {
	Leaderboard findByDomainAndStartDateAndRecurrencePatternAndXpLevelMinAndXpLevelMaxAndXpPointsMinAndXpPointsMaxAndXpPointsPeriodAndXpPointsGranularity(Domain domain, DateTime startDate, String recurrencePattern, Integer xpLevelMin, Integer xpLevelMax, BigDecimal xpPointsMin, BigDecimal xpPointsMax, Integer xpPointsPeriod, Granularity xpPointsGranularity);
	
	List<Leaderboard> findByDomainAndEnabledTrueAndVisibleIsTrue(Domain domain);
	List<Leaderboard> findByDomainAndEnabledTrueAndVisibleIsTrueAndXpLevelMinLessThanEqualAndXpLevelMaxGreaterThanEqual(Domain domain, Integer xpLevel, Integer xpLevel2);
	List<LeaderboardProjection> findByDomainAndEnabledTrueAndVisibleTrue(Domain domain);
	default Leaderboard findOne(Long id) {
		return findById(id).orElse(null);
	}
}
