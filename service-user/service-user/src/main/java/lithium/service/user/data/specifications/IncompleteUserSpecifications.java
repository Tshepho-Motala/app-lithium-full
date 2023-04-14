package lithium.service.user.data.specifications;


import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.IncompleteUser;
import lithium.service.user.data.entities.IncompleteUser_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public class IncompleteUserSpecifications  {
	public static Specification<IncompleteUser> any(final String search) {
		return new Specification<IncompleteUser>() {
			@Override
			public Predicate toPredicate(Root<IncompleteUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
			 		return cb.or(
					cb.like(cb.upper(root.get(IncompleteUser_.firstName)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(IncompleteUser_.lastName)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.lower(root.get(IncompleteUser_.email)), "%" + search.toLowerCase() + "%"),
					cb.like(root.get(IncompleteUser_.gender), "%" + search.toLowerCase() + "%"),
					cb.like(root.get(IncompleteUser_.cellphoneNumber), "%" + search + "%"));
 			}
		};
	}

	public static Specification<IncompleteUser> gender(final String gender) {
		return new Specification<IncompleteUser>() {
			@Override
			public Predicate toPredicate(Root<IncompleteUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(cb.lower(root.get(IncompleteUser_.gender)), gender.toLowerCase());
			}
		};
	}

	public static Specification<IncompleteUser> stage(final String stage) {
		return new Specification<IncompleteUser>() {
			@Override
			public Predicate toPredicate(Root<IncompleteUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
			 	return cb.equal(cb.lower(root.get(IncompleteUser_.stage)), stage);
 			}
		};
	}

	public static Specification<IncompleteUser> stageIn(final List<String> stages) {
		return new Specification<IncompleteUser>() {
			@Override
			public Predicate toPredicate(Root<IncompleteUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return root.get(IncompleteUser_.stage).in(stages);
			}
		};
	}

	public static Specification<IncompleteUser> createdDateAfter(final Date date) {
		return new Specification<IncompleteUser>() {
			@Override
			public Predicate toPredicate(Root<IncompleteUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.greaterThan(root.get(IncompleteUser_.createdDate), date);
			}
		};
	}

	public static Specification<IncompleteUser> createdDateBefore(final Date date) {
		return new Specification<IncompleteUser>() {
			@Override
			public Predicate toPredicate(Root<IncompleteUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.lessThan(root.get(IncompleteUser_.createdDate), date);
			}
		};
	}

	public static Specification<IncompleteUser> createdDateBetween(final Date start, final Date end) {
		return new Specification<IncompleteUser>() {
			@Override
			public Predicate toPredicate(Root<IncompleteUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.between(root.get(IncompleteUser_.createdDate), start, end);
			}
		};
	}
	
	 public static Specification<IncompleteUser> domainIn(final List<Domain> domains) {
		return new Specification<IncompleteUser>() {
			@Override
			public Predicate toPredicate(Root<IncompleteUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<IncompleteUser, Domain> joinDomain = root.join(IncompleteUser_.domain, JoinType.INNER);
				Predicate p = joinDomain.in(domains); 
				return p;
			}
		};
	}
}
