package lithium.service.casino.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.PlayerBonusFreespinHistory;

public interface PlayerBonusFreespinHistoryRepository extends PagingAndSortingRepository<PlayerBonusFreespinHistory, Long> {
	List<PlayerBonusFreespinHistory> findByPlayerBonusHistoryId(Long playerBonusHistoryId);
	PlayerBonusFreespinHistory findByPlayerBonusHistoryIdAndExtBonusId(Long playerBonusHistoryId, Integer extBonusId);
	PlayerBonusFreespinHistory findByPlayerBonusHistoryPlayerBonusPlayerGuidAndExtBonusId(String playerGuid, Integer extBonusId);
}