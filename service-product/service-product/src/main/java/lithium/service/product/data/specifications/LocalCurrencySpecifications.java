package lithium.service.product.data.specifications;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.product.data.entities.LocalCurrency_;
import lithium.service.product.data.entities.Product_;
import lithium.service.product.data.entities.LocalCurrency;
import lithium.service.product.data.entities.Product;

public class LocalCurrencySpecifications {
	public static Specification<LocalCurrency> any(Long productId, String search) {
		return new Specification<LocalCurrency>() {
			@Override
			public Predicate toPredicate(Root<LocalCurrency> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				Join<Product, Catalog> catalogJoin = root.join(Product_.catalog, JoinType.INNER);
				Join<LocalCurrency, Product> productJoin = root.join(LocalCurrency_.product, JoinType.INNER);
				return cb.and(
					cb.equal(productJoin.get(Product_.id), productId),
					cb.or(
						cb.like(cb.upper(root.get(LocalCurrency_.countryCode)), "%" + search.toUpperCase() + "%"),
						cb.like(cb.upper(root.get(LocalCurrency_.currencyCode)), "%" + search.toUpperCase() + "%")
					)
				);
			}
		};
	}
}