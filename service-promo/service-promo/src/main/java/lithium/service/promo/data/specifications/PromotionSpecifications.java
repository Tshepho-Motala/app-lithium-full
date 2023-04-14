package lithium.service.promo.data.specifications;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.PromotionRevision;
import org.springframework.data.jpa.domain.Specification;
import lithium.service.promo.data.entities.Domain;
import lithium.service.promo.data.entities.Domain_;
import lithium.service.promo.data.entities.PromotionRevision_;
import lithium.service.promo.data.entities.Promotion_;

public class PromotionSpecifications {

	public static Specification<Promotion> activePromotionsForDomainAndDate(String domainName, LocalDateTime date) {
		return (root, query, cb) -> {
			Join<Promotion, PromotionRevision> revisionJoin = root.join(Promotion_.current, JoinType.INNER);
			Join<PromotionRevision, Domain> domainJoin = revisionJoin.join(PromotionRevision_.domain, JoinType.INNER);
			return cb.and(
					cb.equal(cb.upper(domainJoin.get(Domain_.name)), domainName.toUpperCase()),
					cb.isFalse(root.get(Promotion_.DELETED)),
					cb.isTrue(root.get(Promotion_.ENABLED)),
					cb.or(
							cb.isNull(revisionJoin.get(PromotionRevision_.START_DATE)),
							cb.lessThanOrEqualTo(revisionJoin.get(PromotionRevision_.START_DATE), date)
					),
					cb.or(
							cb.isNull(revisionJoin.get(PromotionRevision_.END_DATE)),
							cb.greaterThanOrEqualTo(revisionJoin.get(PromotionRevision_.END_DATE), date)
					)
			);
		};
	}

	public static Specification<Promotion> any(String search) {
		return (root, query, cb) -> {
			Join<Promotion, PromotionRevision> revisionJoin = root.join(Promotion_.current, JoinType.INNER);
			Join<PromotionRevision, Domain> domainJoin = revisionJoin.join(PromotionRevision_.domain, JoinType.INNER);
			return cb.or(
					cb.like(cb.upper(domainJoin.get(Domain_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(revisionJoin.get(PromotionRevision_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(revisionJoin.get(PromotionRevision_.description)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(revisionJoin.get(PromotionRevision_.startDate).as(String.class)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(revisionJoin.get(PromotionRevision_.endDate).as(String.class)), "%" + search.toUpperCase() + "%")
			);
		};
	}

	public static Specification<Promotion> domains(List<String> domains) {
		return (root, query, cb) -> {
			Join<Promotion, PromotionRevision> currentJoin = root.join(Promotion_.current, JoinType.INNER);
			Join<PromotionRevision, Domain> domainJoin = currentJoin.join(PromotionRevision_.domain, JoinType.INNER);
			Predicate p = domainJoin.get(Domain_.name).in(domains);
			return p;
		};
	}

	public static Specification<Promotion> findByDomainNameAndStartDateAndEndDateAndXpLevel(String domainName, LocalDateTime startDate, LocalDateTime endDate, Integer xpLevel) {
		return (root, query, cb) -> {
			Join<Promotion, PromotionRevision> currentJoin = root.join(Promotion_.current, JoinType.INNER);
			Join<PromotionRevision, Domain> domainJoin = currentJoin.join(PromotionRevision_.domain, JoinType.INNER);
			Predicate p = cb.equal(cb.upper(domainJoin.get(Domain_.name)), domainName.toUpperCase());
			p = cb.and(p, cb.equal(currentJoin.get(PromotionRevision_.xpLevel), xpLevel));
			p = cb.and(p, cb.or(
					cb.between(currentJoin.get(PromotionRevision_.startDate), startDate, endDate),
					cb.between(currentJoin.get(PromotionRevision_.endDate), startDate, endDate),
					cb.between(cb.literal(startDate), currentJoin.get(PromotionRevision_.startDate), currentJoin.get(PromotionRevision_.endDate))
			));
			return p;
		};
	}

	public static Specification<Promotion> startsBefore(LocalDateTime date) {
		return (root, query, cb) -> {
			Join<Promotion, PromotionRevision> currentJoin = root.join(Promotion_.current, JoinType.INNER);
			return cb.lessThanOrEqualTo(currentJoin.get(PromotionRevision_.startDate), date);
		};
	}

	public static Specification<Promotion> startDate(LocalDateTime date) {
		return (root, query, cb) -> {
			Join<Promotion, PromotionRevision> currentJoin = root.join(Promotion_.current, JoinType.INNER);
			return cb.greaterThanOrEqualTo(currentJoin.get(PromotionRevision_.startDate), date);
		};
	}

	public static Specification<Promotion> endDate(LocalDateTime date) {
		return (root, query, cb) -> {
			Join<Promotion, PromotionRevision> currentJoin = root.join(Promotion_.current, JoinType.INNER);
			return cb.lessThanOrEqualTo(currentJoin.get(PromotionRevision_.endDate), date);
		};
	}

	public static Specification<Promotion> enabled(boolean enabled) {
		return (root, query, cb) -> {
			if(enabled) {
				return cb.isTrue(root.get(Promotion_.enabled));
			}
			return cb.isFalse(root.get(Promotion_.enabled));
		};
	}

	public static Specification<Promotion> deleted(boolean deleted) {
		return (root, query, cb) -> {
			if(deleted) {
				return cb.isTrue(root.get(Promotion_.deleted));
			}
			return cb.isFalse(root.get(Promotion_.deleted));
		};
	}
}