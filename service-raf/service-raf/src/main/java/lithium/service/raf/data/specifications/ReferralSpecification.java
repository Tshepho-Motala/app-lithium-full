package lithium.service.raf.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.raf.data.entities.Domain;
import lithium.service.raf.data.entities.Domain_;
import lithium.service.raf.data.entities.Referral;
import lithium.service.raf.data.entities.Referral_;
import lithium.service.raf.data.entities.Referrer;
import lithium.service.raf.data.entities.Referrer_;

public class ReferralSpecification {
	public static Specification<Referral> any(String search) {
		return new Specification<Referral>() {
			@Override
			public Predicate toPredicate(Root<Referral> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Referral, Domain> domainJoin = root.join(Referral_.domain, JoinType.INNER);
				Join<Referral, Referrer> referrerJoin = root.join(Referral_.referrer, JoinType.INNER);
				return cb.or(
					cb.like(cb.upper(domainJoin.get(Domain_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(referrerJoin.get(Referrer_.playerGuid)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Referral_.playerGuid)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Referral_.timestamp).as(String.class)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Referral_.converted).as(String.class)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<Referral> domain(String domainName) {
		return new Specification<Referral>() {
			@Override
			public Predicate toPredicate(Root<Referral> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Referral, Domain> domainJoin = root.join(Referral_.domain, JoinType.INNER);
				Predicate p = cb.equal(cb.upper(domainJoin.get(Domain_.name)), domainName.toUpperCase());
				return p;
			}
		};
	}
	
	public static Specification<Referral> referrerIs(String referrer) {
		return new Specification<Referral>() {
			@Override
			public Predicate toPredicate(Root<Referral> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Referral, Referrer> referrerJoin = root.join(Referral_.referrer, JoinType.INNER);
				Predicate p = cb.like(cb.upper(referrerJoin.get(Referrer_.playerGuid)), "%" + referrer.toUpperCase() + "%");
				return p;
			}
		};
	}
	
	public static Specification<Referral> convertedIs(boolean converted) {
		return new Specification<Referral>() {
			@Override
			public Predicate toPredicate(Root<Referral> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(Referral_.converted), converted);
				return p;
			}
		};
	}
}
