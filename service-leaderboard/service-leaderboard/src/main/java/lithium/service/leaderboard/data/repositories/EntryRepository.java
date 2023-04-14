package lithium.service.leaderboard.data.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import lithium.service.leaderboard.data.entities.Entry;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardHistory;
import lithium.service.leaderboard.data.entities.User;
import lithium.service.leaderboard.data.projections.EntryProjection;

public interface EntryRepository extends PagingAndSortingRepository<Entry, Long>, JpaSpecificationExecutor<Entry> {
	//Is player currenctly in a tournament?
	Entry findByUserAndLeaderboardHistoryClosedFalseAndLeaderboardHistoryLeaderboardVisibleTrueAndLeaderboardHistoryLeaderboardEnabledTrue(User user);
	EntryProjection findByUserGuidAndLeaderboardHistoryClosedFalseAndLeaderboardHistoryLeaderboardVisibleTrueAndLeaderboardHistoryLeaderboardEnabledTrue(String playerGuid);
	
	Entry findByUserAndLeaderboardHistory(User user, LeaderboardHistory leaderboardHistory);
	EntryProjection findByLeaderboardHistoryAndUser(LeaderboardHistory leaderboardHistory, User user);
	List<Entry> findByLeaderboardHistoryAndRank(LeaderboardHistory leaderboardHistory, Integer rank);
	List<Entry> findByLeaderboardHistoryAndRankNotInAndRankLessThanEqual(LeaderboardHistory leaderboardHistory, List<Integer> rank, Integer rankLessThanEqualTo, Pageable page);
	List<Entry> findByLeaderboardHistoryAndRankGreaterThan(LeaderboardHistory leaderboardHistory, Integer rank);
	
	@Query( //"select o.role.category from GRD o where o.group.id = :groupId group by o.role.category"
		"SELECT new Entry(e.id, e.rank, e.score, e.points, e.user, lh) from Entry e "+ 
		"LEFT JOIN e.leaderboardHistory lh "+
//		"ON e.leaderboardHistory.id = lh.id "+
		"  LEFT JOIN lh.leaderboard l "+
//		"  ON lh.leaderboard.id = l.id "+
//		"  LEFT JOIN lh.period p "+
//		"  ON lh.period.id = p.id "+
		"WHERE 1=1 "+ //lh.closed = FALSE "+
		"AND e.rank <= l.amount "+
		"AND ( "+
			"lh.startDate <= NOW() "+
			"AND lh.endDate >= NOW() "+
			"AND NOW() BETWEEN lh.startDate AND lh.endDate "+
		") "+
		"AND l = :leaderboard "+
		"ORDER BY e.rank ASC "
	)
	List<EntryProjection> findTopEntries(@Param("leaderboard") Leaderboard leaderboard);

	int countByLeaderboardHistory(LeaderboardHistory leaderboardHistory);
	List<Entry> findByLeaderboardHistory(LeaderboardHistory leaderboardHistory);
	
	@Query(nativeQuery = true, name = "rankEntryMapping")
	List<Entry> denseRankEntries(@Param("lhid") Long leaderboardHistoryId);
}