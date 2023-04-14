package lithium.service.casino.data.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusRulesGamesPercentages;

public interface BonusRulesGamesPercentagesRepository extends PagingAndSortingRepository<BonusRulesGamesPercentages, Long> {
	BonusRulesGamesPercentages findByGameGuidAndBonusRevisionId(String gameGuid, Long bonusRevisionId);
	BonusRulesGamesPercentages findByGameCategoryAndBonusRevisionId(String gameGuid, Long bonusRevisionId);
	BonusRulesGamesPercentages findByBonusRevisionIdAndGameGuid(Long bonusRevisionId, String gameGuid);
	List<BonusRulesGamesPercentages> findByBonusRevisionIdAndGameGuidIsNullAndGameCategoryIsNotNull(Long bonusRevisionId);
	List<BonusRulesGamesPercentages> findByBonusRevisionIdAndGameGuidIsNotNullOrderByPercentageDescGameGuidDesc(Long bonusRevisionId);
	List<BonusRulesGamesPercentages> findByBonusRevisionId(Long bonusRevisionId);
	
	@Modifying
	@Transactional
	void deleteByBonusRevisionIdAndGameGuidIsNotNull(Long bonusRevisionId);
	@Modifying
	@Transactional
	void deleteById(Long id);
	
	@Modifying
	@Transactional
	void deleteByBonusRevisionIdAndGameGuidIsNullAndGameCategoryIsNotNull(Long bonusRevisionId);
}