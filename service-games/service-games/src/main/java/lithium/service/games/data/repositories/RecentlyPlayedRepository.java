package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.RecentlyPlayed;
import lithium.service.games.data.entities.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface RecentlyPlayedRepository extends PagingAndSortingRepository<RecentlyPlayed, Long> {
	List<RecentlyPlayed> findRecentlyPlayedByUserOrderByLastUsedAsc(User user);

	Optional<RecentlyPlayed> findByUserAndGame(User user, Game game);
}
