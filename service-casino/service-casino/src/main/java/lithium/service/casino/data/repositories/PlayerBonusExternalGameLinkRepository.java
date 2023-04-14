package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.PlayerBonusExternalGameLink;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PlayerBonusExternalGameLinkRepository extends PagingAndSortingRepository<PlayerBonusExternalGameLink, Long> {
	List<PlayerBonusExternalGameLink> findByPlayerBonusHistoryId(Long playerBonusHistoryId);
}
