package lithium.service.casino.data.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusRequirementsDeposit;

public interface BonusRequirementsDepositRepository extends PagingAndSortingRepository<BonusRequirementsDeposit, Long> {
	List<BonusRequirementsDeposit> findByBonusRevisionId(Long bonusRevisionId);
	BonusRequirementsDeposit findByBonusRevisionIdAndMaxDepositGreaterThanAndMinDepositLessThanEqual(Long bonusRevisionId, Long depositAmount, Long depositAmount2);
	BonusRequirementsDeposit findByBonusRevisionIdAndMinDepositLessThanEqualAndMaxDepositIsNull(Long bonusRevisionId, Long depositAmount);
	BonusRequirementsDeposit findTop1ByBonusRevisionIdOrderByMaxDepositDesc(Long bonusRevisionId);
	
	@Modifying
	@Transactional
	void deleteByBonusRevisionId(Long bonusRevisionId);
}