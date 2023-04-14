package lithium.service.casino.data.specifications;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lithium.service.casino.data.entities.BonusExternalGameConfig;
import lithium.service.casino.data.entities.BonusExternalGameConfig_;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.entities.Domain_;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.entities.PlayerBonus_;
import org.springframework.data.jpa.domain.Specification;
import lithium.service.casino.data.entities.BonusRevision_;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.entities.PlayerBonusHistory_;
import lithium.specification.JoinableSpecification;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

@Slf4j
public class PlayerBonusHistorySpecification {
	
	public static Specification<PlayerBonusHistory> byBonusRevisionId(final Long bonusRevisionId) {
		return new JoinableSpecification<PlayerBonusHistory>() {
			@Override
			public Predicate toPredicate(Root<PlayerBonusHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				log.debug("bonusRevisionId: "+bonusRevisionId);
				return cb.and(
					cb.isNotNull(this.joinList(root, PlayerBonusHistory_.playerBonus, JoinType.LEFT)),
					cb.equal(this.joinList(root, PlayerBonusHistory_.bonus, JoinType.LEFT).get(BonusRevision_.id), bonusRevisionId)
				);
			}
		};
	}

	public static Specification<PlayerBonusHistory> playerGuid(final String playerGuid) {
		return (root, query, cb) -> {
			Join<PlayerBonusHistory, PlayerBonus> entityJoin = root.join(PlayerBonusHistory_.playerBonus, JoinType.INNER);
			Predicate p = entityJoin.get(PlayerBonus_.playerGuid).in(playerGuid);
			return p;
		};
	}

	public static Specification<PlayerBonusHistory> startedDateRangeStart(final Date dateRangeStart) {
		return (root, query, cb) -> {
			Predicate p = cb.greaterThanOrEqualTo(root.get(PlayerBonusHistory_.startedDate).as(Date.class), new java.sql.Date(dateRangeStart.getTime()));
			return p;
		};
	}

	public static Specification<PlayerBonusHistory> startedDateRangeEnd(final Date dateRangeEnd) {
		return (root, query, cb) -> {
			Predicate p = cb.lessThanOrEqualTo(root.get(PlayerBonusHistory_.startedDate).as(Date.class), new java.sql.Date(dateRangeEnd.getTime()));
			return p;
		};
	}

	public static Specification<PlayerBonusHistory> granted() {
		return (root, criteriaQuery, criteriaBuilder) -> {
			Predicate g = criteriaBuilder.isTrue(root.get(PlayerBonusHistory_.completed));
			return g;
		};
	}
	public static Specification<PlayerBonusHistory> expired() {
		return (root, criteriaQuery, criteriaBuilder) -> {
			Predicate g = criteriaBuilder.isTrue(root.get(PlayerBonusHistory_.expired));
			return g;
		};
	}
	public static Specification<PlayerBonusHistory> cancelled() {
		return (root, criteriaQuery, criteriaBuilder) -> {
			Predicate g = criteriaBuilder.isTrue(root.get(PlayerBonusHistory_.cancelled));
			return g;
		};
	}

	public static Specification<PlayerBonusHistory> active() {
		return (root, criteriaQuery, criteriaBuilder) -> {

			return criteriaBuilder.and(criteriaBuilder.isFalse(root.get(PlayerBonusHistory_.cancelled)),
					criteriaBuilder.isFalse(root.get(PlayerBonusHistory_.completed)),
					criteriaBuilder.isFalse(root.get(PlayerBonusHistory_.expired)));
		};
	}

	public static Specification<PlayerBonusHistory> bonusCodes(final String[] bonusCodes) {
		return (root, query, cb) -> {
			Join<PlayerBonusHistory, BonusRevision> entityJoin = root.join(PlayerBonusHistory_.bonus, JoinType.INNER);
			Predicate p = entityJoin.get(BonusRevision_.bonusCode).in(bonusCodes);
			return p;
		};
	}

	public static Specification<PlayerBonusHistory> zeroWagerRequirement() {
		return (root, query, cb) -> {
			Join<PlayerBonusHistory, BonusRevision> entityJoin = root.join(PlayerBonusHistory_.bonus, JoinType.INNER);
			Predicate p = entityJoin.get(BonusRevision_.freeMoneyWagerRequirement).isNull();
			return p;
		};
	}

	public static Specification<PlayerBonusHistory> byDomains(final List<String> domains) {
		return (root, query, cb) -> {
			Join<PlayerBonusHistory, BonusRevision> entityJoin = root.join(PlayerBonusHistory_.bonus, JoinType.INNER);
			Join<BonusRevision, Domain> tableJoin = entityJoin.join(BonusRevision_.domain, JoinType.INNER);
			return tableJoin.get(Domain_.name).in(domains);
		};
	}

	public static Specification<PlayerBonusHistory> notCompleted() {
		return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(PlayerBonusHistory_.completed));
	}

	public static Specification<PlayerBonusHistory> notExpired() {
		return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(PlayerBonusHistory_.expired));
	}

	public static Specification<PlayerBonusHistory> notCancelled() {
		return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(PlayerBonusHistory_.cancelled));
	}

	public static Specification<PlayerBonusHistory> forProvider(String provider) {
		return (root, criteriaQuery, criteriaBuilder) -> {
			Join<PlayerBonusHistory, BonusRevision> bonusRevisionJoin = root.join(PlayerBonusHistory_.bonus);
			ListJoin<BonusRevision, BonusExternalGameConfig> bonusRevisionBonusExternalGameConfigListJoin = bonusRevisionJoin.join(BonusRevision_.bonusExternalGameConfigs);
			return criteriaBuilder.equal(bonusRevisionBonusExternalGameConfigListJoin.get(BonusExternalGameConfig_.provider), provider);
		};
	}

	public static Specification<PlayerBonusHistory> forCampaign(Long campaignId) {
		return (root, criteriaQuery, criteriaBuilder) -> {
			Join<PlayerBonusHistory, BonusRevision> bonusRevisionJoin = root.join(PlayerBonusHistory_.bonus);
			ListJoin<BonusRevision, BonusExternalGameConfig> bonusRevisionBonusExternalGameConfigListJoin = bonusRevisionJoin.join(BonusRevision_.bonusExternalGameConfigs);
			return criteriaBuilder.equal(bonusRevisionBonusExternalGameConfigListJoin.get(BonusExternalGameConfig_.campaignId), campaignId);
		};
	}
}