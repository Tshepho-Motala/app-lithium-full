package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameChannel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface GameChannelRepository extends PagingAndSortingRepository<GameChannel, Long> {

    List<GameChannel> findGameChannelByGame(Game game);
}