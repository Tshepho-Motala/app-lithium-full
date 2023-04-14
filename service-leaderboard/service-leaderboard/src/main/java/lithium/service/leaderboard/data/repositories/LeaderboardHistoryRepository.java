package lithium.service.leaderboard.data.repositories;

import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardHistory;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaderboardHistoryRepository extends PagingAndSortingRepository<LeaderboardHistory, Long>, JpaSpecificationExecutor<LeaderboardHistory> {
	List<LeaderboardHistory> findByClosedFalseAndLeaderboardEnabledTrueAndEndDateBefore(DateTime date);
	
	@Query(
		"SELECT new LeaderboardHistory(lh.id, lh.leaderboard) "
		+ "from LeaderboardHistory lh "
		+ "LEFT JOIN lh.leaderboard l "
		+ "WHERE lh.closed = false "
		+ "AND l.enabled = true "
		+ "AND ( "
			+ "lh.startDate <= :date "
			+ "AND lh.endDate >= :date "
			+ "AND :date BETWEEN lh.startDate AND lh.endDate "
		+ ") "
	)
	List<LeaderboardHistory> findCurrentOpen(@Param("date") DateTime date);

	LeaderboardHistory findByLeaderboardAndClosedFalseAndLeaderboardEnabledTrueAndStartDateBeforeAndEndDateAfter(Leaderboard leaderboard, DateTime date1, DateTime date2);
}