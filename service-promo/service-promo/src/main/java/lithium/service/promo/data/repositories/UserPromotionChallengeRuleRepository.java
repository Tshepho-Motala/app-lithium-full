package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.PromotionStat;
import lithium.service.promo.data.entities.UserPromotionChallengeRule;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface UserPromotionChallengeRuleRepository extends PagingAndSortingRepository<UserPromotionChallengeRule, Long>, JpaSpecificationExecutor<UserPromotionChallengeRule> {
//	List<RuleFE> findByRuleTypeAndRuleActionAndRuleIdentifierAndRuleValue(String type, String action, String identifier, String value);
	//	List<RuleFE> findByUserMissionChallenge(ChallengeFE ChallengeFE);

	@Transactional
	@Modifying
	@Query("UPDATE #{#entityName} o " +
			"SET o.percentage = :percentage " +
			"WHERE o.id = :id")
	void updatePercentage(@Param("id") Long id, @Param("percentage") BigDecimal percentage);

	@Transactional
	@Modifying
	@Query("UPDATE #{#entityName} o " +
			"SET o.percentage = 100 " +
			", o.completed = :date " +
			", o.ruleComplete = TRUE " +
			"WHERE o.id = :id")
	void completeRule(@Param("id") Long id, @Param("date") LocalDateTime completed);

	@Transactional
	@Modifying
	@Query("UPDATE #{#entityName} o " +
			"SET o.promotionStat = :promotionStat " +
			"WHERE o.id = :id")
	void updatePromotionStat(@Param("id") Long id, @Param("promotionStat") PromotionStat promotionStat);

	@Transactional
	@Modifying
	@Query("UPDATE #{#entityName} o " +
			"SET o.started = :date " +
			"WHERE o.id = :id")
	void updateRuleStarted(@Param("id") Long id, @Param("date") LocalDateTime started);

}