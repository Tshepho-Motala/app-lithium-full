package lithium.service.casino.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.BonusRevision;

public interface BonusRevisionRepository extends PagingAndSortingRepository<BonusRevision, Long>, JpaSpecificationExecutor<BonusRevision> {
	BonusRevision findByBonusCode(String bonusCode);
	BonusRevision findByBonusCodeAndDomainName(String bonusCode, String domainName);
	BonusRevision findTop1ByBonusCodeAndBonusTypeAndDomainNameAndEnabledAndDeletedOrderByIdDesc(String bonusCode, Integer bonusType, String domainName, Boolean enabled, Boolean deleted);
	Page<BonusRevision> findByBonusIdOrderByIdDesc(Pageable page, Long bonusId);

	default BonusRevision findOne(Long id) {
		return findById(id).orElse(null);
	}

}