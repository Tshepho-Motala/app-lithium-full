package lithium.service.games.data.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameUserStatus;
import lithium.service.games.data.entities.User;

public interface GameUserStatusRepository extends PagingAndSortingRepository<GameUserStatus, Long>, JpaSpecificationExecutor<GameUserStatus> {
	Page<GameUserStatus> findAllByGame(Game game, Pageable pageRequest);
	GameUserStatus findByGameAndUser(Game game, User user);
	GameUserStatus findByGameGuidAndUser(String gameGuid, User user);
	List<GameUserStatus> findByUser(User user);
	List<GameUserStatus> findByUserAndLockedFalse(User user);
	
//	Do not uncomment.
//	@CacheEvict(cacheNames="lithium.service.games.services.getUserGameList", key="#result.getUser().guid()")
	@Override
	<S extends GameUserStatus> S save(S entity);

}