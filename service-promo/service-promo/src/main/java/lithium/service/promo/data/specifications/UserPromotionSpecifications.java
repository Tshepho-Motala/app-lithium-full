package lithium.service.promo.data.specifications;

import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import lithium.service.promo.data.entities.Domain;
import lithium.service.promo.data.entities.Domain_;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.entities.PromotionRevision_;
import lithium.service.promo.data.entities.Promotion_;
import lithium.service.promo.data.entities.User;
import lithium.service.promo.data.entities.UserPromotion;
import lithium.service.promo.data.entities.UserPromotion_;
import lithium.service.promo.data.entities.User_;
import org.springframework.data.jpa.domain.Specification;

public class UserPromotionSpecifications {
	private UserPromotionSpecifications() {
		throw new IllegalStateException("Utility class");
	}

	public static Specification<UserPromotion> domains(List<String> domains) {
		return (root, query, cb) -> {
			Join<UserPromotion, PromotionRevision> missionJoin = root.join(UserPromotion_.promotionRevision, JoinType.INNER);
			Join<PromotionRevision, Domain> domainJoin = missionJoin.join(PromotionRevision_.domain, JoinType.INNER);
			Predicate p = domainJoin.get(Domain_.name).in(domains);
			return p;
		};
	}

	public static Specification<UserPromotion> user(String userGuid) {
		return (root, query, cb) -> {
			Join<UserPromotion, User> userJoin = root.join(UserPromotion_.user, JoinType.INNER);
			return cb.equal(cb.upper(userJoin.get(User_.guid)), userGuid.toUpperCase());
		};
	}

	public static Specification<UserPromotion> startedDateRangeStart(Date startedDateRangeStart) {
		return (root, query, cb) -> {
			Predicate p = cb.greaterThanOrEqualTo(root.get(UserPromotion_.started).as(Date.class), startedDateRangeStart);
			return p;
		};
	}

	public static Specification<UserPromotion> startedDateRangeEnd(Date startedDateRangeEnd) {
		return (root, query, cb) -> {
			Predicate p = cb.lessThanOrEqualTo(root.get(UserPromotion_.started).as(Date.class), startedDateRangeEnd);
			return p;
		};
	}

	public static Specification<UserPromotion> isActive() {
		return (root, query, cb) -> {
			Predicate p = cb.isTrue(root.get(UserPromotion_.active));
			return p;
		};
	}

	public static Specification<UserPromotion> isCompleted() {
		return (root, query, cb) -> {
			Predicate p = cb.isTrue(root.get(UserPromotion_.promotionComplete));
			return p;
		};
	}

	public static Specification<UserPromotion> completed(boolean completed) {
		return (root, query, cb) -> {
			if (completed) {
				return cb.isTrue(root.get(UserPromotion_.promotionComplete));
			} else {
				return cb.isFalse(root.get(UserPromotion_.promotionComplete));
			}
		};
	}

	public static Specification<UserPromotion> promotionRevision(PromotionRevision promotionRevision) {
		return (root, query, cb) -> {
			Predicate p = cb.equal(root.get(UserPromotion_.promotionRevision), promotionRevision);
			return p;
		};
	}

	public static Specification<UserPromotion> isCurrent() {
		return (root, query, cb) -> {
			Join<UserPromotion, PromotionRevision> revisionJoin = root.join(UserPromotion_.promotionRevision, JoinType.INNER);
			Join<PromotionRevision, Promotion> missionJoin =  revisionJoin.join(PromotionRevision_.promotion, JoinType.INNER);
			return cb.equal(root.get(UserPromotion_.promotionRevision), missionJoin.get(Promotion_.current));
		};
	}
}
