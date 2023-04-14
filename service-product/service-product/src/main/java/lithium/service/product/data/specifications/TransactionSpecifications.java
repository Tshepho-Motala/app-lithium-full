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
import lithium.service.product.data.entities.Transaction;
import lithium.service.product.data.entities.Transaction_;
import lithium.service.product.data.entities.User;
import lithium.service.product.data.entities.User_;

public class TransactionSpecifications {
	public static Specification<Transaction> any(String search) {
		return new Specification<Transaction>() {
			@Override
			public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				Join<Transaction, Catalog> catalogJoin = root.join(Transaction_.catalog, JoinType.INNER);
				Join<Transaction, Domain> domainJoin = root.join(Transaction_.domain, JoinType.INNER);
				Join<Transaction, User> userJoin = root.join(Transaction_.user, JoinType.INNER);
				Join<Transaction, Product> productJoin = root.join(Transaction_.product, JoinType.INNER);
				return cb.or(
					cb.like(cb.upper(domainJoin.get(Domain_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Transaction_.currencyCode)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Transaction_.domainMethodName)), "%" + search.toUpperCase() + "%"),
					cb.like(root.get(Transaction_.cashierTransactionId).as(String.class), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(userJoin.get(User_.guid)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(productJoin.get(Product_.guid)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(productJoin.get(Product_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(productJoin.get(Product_.description)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<Transaction> domains(List<String> domains) {
		return new Specification<Transaction>() {
			@Override
			public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Transaction, Domain> domainJoin = root.join(Transaction_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}
}