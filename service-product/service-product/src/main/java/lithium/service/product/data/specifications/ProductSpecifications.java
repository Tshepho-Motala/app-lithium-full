package lithium.service.product.data.specifications;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.product.data.entities.Domain;
import lithium.service.product.data.entities.Domain_;
import lithium.service.product.data.entities.Product;
import lithium.service.product.data.entities.Product_;

public class ProductSpecifications {
	public static Specification<Product> any(String search) {
		return new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				Join<Product, Catalog> catalogJoin = root.join(Product_.catalog, JoinType.INNER);
				Join<Product, Domain> domainJoin = root.join(Product_.domain, JoinType.INNER);
				return cb.or(
					cb.like(cb.upper(domainJoin.get(Domain_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Product_.guid)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Product_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Product_.description)), "%" + search.toUpperCase() + "%")
//					cb.like(cb.upper(catalogJoin.get(Catalog_.name)), "%" + search.toUpperCase() + "%"),
//					cb.like(cb.upper(catalogJoin.get(Catalog_.description)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<Product> domains(List<String> domains) {
		return new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				Join<Product, Catalog> catalogJoin = root.join(Product_.catalog, JoinType.INNER);
				Join<Product, Domain> domainJoin = root.join(Product_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}
	
	public static Specification<Product> enabled(Boolean enabled) {
		return new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(Product_.enabled), enabled);
				return p;
			}
		};
	}
}