package lithium.service.promo.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lithium.service.promo.data.entities.PromotionRevision;
import org.springframework.data.jpa.domain.Specification;

import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.PromotionRevision_;

public class PromotionRevisionSpecifications {

	public static Specification<PromotionRevision> revisionsByPromotion(Promotion promotion) {
		return new Specification<PromotionRevision>() {
			@Override
			public Predicate toPredicate(Root<PromotionRevision> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(PromotionRevision_.promotion), promotion);
				return p;
			}
		};
	}
}
