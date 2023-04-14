package lithium.service.leaderboard.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardPlaceNotification;

public interface LeaderboardPlaceNotificationRepository extends PagingAndSortingRepository<LeaderboardPlaceNotification, Long>, JpaSpecificationExecutor<LeaderboardPlaceNotification> {
	LeaderboardPlaceNotification findByLeaderboardAndRank(Leaderboard leaderboard, Integer rank);

	default LeaderboardPlaceNotification findOne(Long id) {
		return findById(id).orElse(null);
	}
}