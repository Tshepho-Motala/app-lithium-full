package lithium.service.casino.data.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import lithium.service.casino.data.entities.Bonus;

public interface BonusRepository extends PagingAndSortingRepository<Bonus, Long>, JpaSpecificationExecutor<Bonus> {
	Bonus findByCurrentId(Long bonusRevisionId);
	Bonus findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentEnabledTrue(String bonusCode, String domainName, Integer bonusType);
	
	Bonus findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentBonusTriggerTypeAndCurrentEnabledTrueAndCurrentFreeMoneyWagerRequirementIsNull(String bonusCode, String domainName, Integer bonusType, Integer bonusTriggerType);
	Bonus findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentBonusTriggerTypeAndCurrentEnabledTrue(String bonusCode, String domainName, Integer bonusType, Integer bonusTriggerType);

	Page<Bonus> findByCurrentEnabledAndCurrentDomainNameInAndCurrentBonusCodeContainingAndCurrentBonusNameContainingOrCurrentIsNullAndEditEnabledAndEditDomainNameInAndEditBonusCodeContainingAndEditBonusNameContaining(Pageable page, Boolean enabled, List<String> domainNames, String searchCode, String searchName, Boolean editEnabled, List<String> editDomainNames, String editSearchCode, String editSearchName);
	Page<Bonus> findByCurrentEnabledAndCurrentDomainNameInAndCurrentBonusTypeAndCurrentBonusCodeContainingAndCurrentBonusNameContainingOrCurrentIsNullAndEditEnabledAndEditDomainNameInAndEditBonusTypeAndEditBonusCodeContainingAndEditBonusNameContaining(Pageable page, Boolean enabled, List<String> domainNames, Integer bonusType, String searchCode, String searchName, Boolean editEnabled, List<String> editDomainNames, Integer editBonusType, String editSearchCode, String editSearchName);
	Page<Bonus> findByCurrentDomainNameInAndCurrentBonusCodeContainingAndCurrentBonusNameContainingOrCurrentIsNullAndEditDomainNameInAndEditBonusCodeContainingAndEditBonusNameContaining(Pageable page, List<String> domainNames, String searchCode, String searchName, List<String> editDomainNames, String editSearchCode, String editSearchName);
	Page<Bonus> findByCurrentDomainNameInAndCurrentBonusTypeAndCurrentBonusCodeContainingAndCurrentBonusNameContainingOrCurrentIsNullAndEditDomainNameInAndEditBonusTypeAndEditBonusCodeContainingAndEditBonusNameContaining(Pageable page, List<String> domainNames, Integer bonusType, String searchCode, String searchName, List<String> editDomainNames, Integer editBonusType, String editSearchCode, String editSearchName);
	
	List<Bonus> findTop50ByCurrentBonusTypeAndCurrentBonusCodeIgnoreCaseContainingOrCurrentBonusNameIgnoreCaseContainingOrderByCurrentBonusCode(Integer bonusType, String bonusName, String bonusCode);
	
	List<Bonus> findByCurrentBonusTypeAndCurrentBonusTriggerTypeAndCurrentDomainNameAndCurrentEnabledTrueAndCurrentPublicViewTrue(Integer bonusType, Integer triggerType, String domainName);
	List<Bonus> findByCurrentBonusTypeAndCurrentBonusTriggerTypeAndCurrentDomainNameAndCurrentEnabledTrue(Integer bonusType, Integer triggerType, String domainName);
	List<Bonus> findByCurrentBonusTypeAndCurrentBonusTriggerTypeAndCurrentDomainNameAndCurrentEnabledTrueAndCurrentFreeMoneyWagerRequirementIsNull(Integer bonusType, Integer triggerType, String domainName);
	List<Bonus> findByCurrentBonusTypeAndCurrentDomainNameAndCurrentEnabledTrueAndCurrentBonusTriggerTypeAndCurrentTriggerAmountAndCurrentTriggerGranularity(Integer bonusType, String domainName, Integer bonusTriggerType, Long triggerAmount, Integer triggerGranularity);
//	List<Bonus> findByCurrentBonusTypeAndCurrentDomainNameAndCurrentEnabledTrueAndCurrentBonusTriggerTypeAndCurrentTriggerAmountLessThanEqualAndCurrentTriggerGranularityOrderByCurrentTriggerAmount(Integer bonusType, String domainName, Integer bonusTriggerType, Long triggerAmount, Integer triggerGranularity);

	default Bonus findOne(Long id) {
		return findById(id).orElse(null);
	}

}