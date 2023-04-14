package lithium.service.pushmsg.data.specifications;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.pushmsg.data.entities.Domain;
import lithium.service.pushmsg.data.entities.PushMsg;
import lithium.service.pushmsg.data.entities.PushMsg_;
import lithium.service.pushmsg.data.entities.User;
import lithium.service.pushmsg.data.entities.User_;

public class PushMsgSpecification {
	public static Specification<PushMsg> domainIn(List<Domain> domains) {
		return new Specification<PushMsg>() {
			@Override
			public Predicate toPredicate(Root<PushMsg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = root.get(PushMsg_.domain).in(domains);
				return p;
			}
		};
	}
	
	public static Specification<PushMsg> domainIs(Domain domain) {
		return new Specification<PushMsg>() {
			@Override
			public Predicate toPredicate(Root<PushMsg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(PushMsg_.domain), domain);
				return p;
			}
		};
	}
	
	public static Specification<PushMsg> any(String value) {
		return new Specification<PushMsg>() {
			@Override
			public Predicate toPredicate(Root<PushMsg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(PushMsg_.createdDate).as(String.class)), "%" + value.toUpperCase() + "%");
				p = cb.or(p, cb.like(cb.upper(root.get(PushMsg_.sentDate).as(String.class)), "%" + value.toUpperCase() + "%"));
				Join<PushMsg, User> joinUser = root.join(PushMsg_.users, JoinType.INNER);
				p = cb.or(p, cb.like(cb.upper(joinUser.get(User_.guid)), "%" + value.toUpperCase() + "%"));
				return p;
			}
		};
	}
	
	public static Specification<PushMsg> sentDateIsNull() {
		return new Specification<PushMsg>() {
			@Override
			public Predicate toPredicate(Root<PushMsg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.isNull(root.get(PushMsg_.sentDate));
				return p;
			}
		};
	}
	
	public static Specification<PushMsg> failedFalse() {
		return new Specification<PushMsg>() {
			@Override
			public Predicate toPredicate(Root<PushMsg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.isFalse(root.get(PushMsg_.failed));
				return p;
			}
		};
	}
	
	public static Specification<PushMsg> user(User user) {
		return new Specification<PushMsg>() {
			@Override
			public Predicate toPredicate(Root<PushMsg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(PushMsg_.users), user);
				return p;
			}
		};
	}
	
	public static Specification<PushMsg> createdDateStart(Date dateStart) {
		return new Specification<PushMsg>() {
			@Override
			public Predicate toPredicate(Root<PushMsg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(PushMsg_.createdDate), dateStart);
				return p;
			}
		};
	}

	public static Specification<PushMsg> createdDateEnd(Date dateEnd) {
		return new Specification<PushMsg>() {
			@Override
			public Predicate toPredicate(Root<PushMsg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(PushMsg_.createdDate), dateEnd);
				return p;
			}
		};
	}
}