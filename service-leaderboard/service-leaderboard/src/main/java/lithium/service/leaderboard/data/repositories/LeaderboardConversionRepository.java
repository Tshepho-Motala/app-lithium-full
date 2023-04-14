package lithium.service.leaderboard.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.leaderboard.client.objects.Type;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardConversion;

public interface LeaderboardConversionRepository extends PagingAndSortingRepository<LeaderboardConversion, Long>, JpaSpecificationExecutor<LeaderboardConversion> {
	LeaderboardConversion findByLeaderboardAndType(Leaderboard leaderboard, Type type);

	default LeaderboardConversion findOne(Long id) {
		return findById(id).orElse(null);
	}
}