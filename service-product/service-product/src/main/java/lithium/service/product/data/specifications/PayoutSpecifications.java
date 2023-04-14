package lithium.service.product.data.specifications;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.product.data.entities.Payout_;
import lithium.service.product.data.entities.Product_;
import lithium.service.product.data.entities.Payout;
import lithium.service.product.data.entities.Product;

public class PayoutSpecifications {
	public static Specification<Payout> any(Long productId, String search) {
		return new Specification<Payout>() {
			@Override
			public Predicate toPredicate(Root<Payout> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				Join<Product, Catalog> catalogJoin = root.join(Product_.catalog, JoinType.INNER);
				Join<Payout, Product> productJoin = root.join(Payout_.product, JoinType.INNER);
				return cb.and(
					cb.equal(productJoin.get(Product_.id), productId),
					cb.or(
						cb.like(cb.upper(root.get(Payout_.bonusCode)), "%" + search.toUpperCase() + "%"),
						cb.like(cb.upper(root.get(Payout_.currencyCode)), "%" + search.toUpperCase() + "%")
					)
				);
			}
		};
	}
}