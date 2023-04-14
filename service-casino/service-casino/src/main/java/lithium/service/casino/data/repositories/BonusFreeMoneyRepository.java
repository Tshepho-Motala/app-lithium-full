package lithium.service.casino.data.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusFreeMoney;

public interface BonusFreeMoneyRepository extends PagingAndSortingRepository<BonusFreeMoney, Long> {
	List<BonusFreeMoney> findByBonusRevisionId(Long bonusRevisionId);

	@Modifying
	@Transactional
	void deleteByBonusRevisionId(Long bonusRevisionId);
	@Modifying
	@Transactional
	void deleteById(Long id);
}