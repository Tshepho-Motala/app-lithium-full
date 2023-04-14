package lithium.service.sms.data.specifications;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.sms.data.entities.Domain;
import lithium.service.sms.data.entities.SMS;
import lithium.service.sms.data.entities.SMS_;
import lithium.service.sms.data.entities.User;
import lithium.service.sms.data.entities.User_;

public class SMSSpecification {
	public static Specification<SMS> domainIn(List<Domain> domains) {
		return new Specification<SMS>() {
			@Override
			public Predicate toPredicate(Root<SMS> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = root.get(SMS_.domain).in(domains);
				return p;
			}
		};
	}
	
	public static Specification<SMS> any(String value) {
		return new Specification<SMS>() {
			@Override
			public Predicate toPredicate(Root<SMS> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(SMS_.createdDate).as(String.class)), "%" + value.toUpperCase() + "%");
				p = cb.or(p, cb.like(cb.upper(root.get(SMS_.sentDate).as(String.class)), "%" + value.toUpperCase() + "%"));
				p = cb.or(p, cb.like(cb.upper(root.get(SMS_.from)), "%" + value.toUpperCase() + "%"));
				p = cb.or(p, cb.like(cb.upper(root.get(SMS_.to)), "%" + value.toUpperCase() + "%"));
				Join<SMS, User> joinUser = root.join(SMS_.user, JoinType.INNER);
				p = cb.or(p, cb.like(cb.upper(joinUser.get(User_.guid)), "%" + value.toUpperCase() + "%"));
				return p;
			}
		};
	}
	
	public static Specification<SMS> sentDateIsNull() {
		return new Specification<SMS>() {
			@Override
			public Predicate toPredicate(Root<SMS> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.isNull(root.get(SMS_.sentDate));
				return p;
			}
		};
	}
	
	public static Specification<SMS> failedFalse() {
		return new Specification<SMS>() {
			@Override
			public Predicate toPredicate(Root<SMS> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.isFalse(root.get(SMS_.failed));
				return p;
			}
		};
	}
	
	public static Specification<SMS> user(User user) {
		return new Specification<SMS>() {
			@Override
			public Predicate toPredicate(Root<SMS> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(SMS_.user), user);
				return p;
			}
		};
	}
	
	public static Specification<SMS> createdDateStart(Date dateStart) {
		return new Specification<SMS>() {
			@Override
			public Predicate toPredicate(Root<SMS> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(SMS_.createdDate), dateStart);
				return p;
			}
		};
	}

	public static Specification<SMS> createdDateEnd(Date dateEnd) {
		return new Specification<SMS>() {
			@Override
			public Predicate toPredicate(Root<SMS> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(SMS_.createdDate), dateEnd);
				return p;
			}
		};
	}
}