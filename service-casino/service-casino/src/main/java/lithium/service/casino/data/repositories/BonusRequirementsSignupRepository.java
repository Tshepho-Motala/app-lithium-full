package lithium.service.casino.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusRequirementsSignup;

public interface BonusRequirementsSignupRepository extends PagingAndSortingRepository<BonusRequirementsSignup, Long> {
	List<BonusRequirementsSignup> findByBonusRevisionId(Long bonusRevisionId);
}