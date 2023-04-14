package lithium.service.mail.data.specifications;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lithium.service.mail.data.entities.EmailTemplate;
import org.springframework.data.jpa.domain.Specification;

import lithium.service.mail.data.entities.Domain;
import lithium.service.mail.data.entities.Email;
import lithium.service.mail.data.entities.Email_;
import lithium.service.mail.data.entities.User;

public class EmailSpecification {
	public static Specification<Email> domainIn(List<Domain> domains) {
		return new Specification<Email>() {
			@Override
			public Predicate toPredicate(Root<Email> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = root.get(Email_.domain).in(domains);
				return p;
			}
		};
	}
	// This query is not optimised and will cause havoc on large data sets.
	// I'm choosing to remove as searching by from, to, bcc, subject, etc does not seem so useful.
	// We already have date filters. And user specific mail lists.
//	public static Specification<Email> any(String value) {
//		return new Specification<Email>() {
//			@Override
//			public Predicate toPredicate(Root<Email> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				Predicate p = cb.like(cb.upper(root.get(Email_.createdDate).as(String.class)), "%" + value.toUpperCase() + "%");
//				p = cb.or(p, cb.like(cb.upper(root.get(Email_.sentDate).as(String.class)), "%" + value.toUpperCase() + "%"));
//				p = cb.or(p, cb.like(cb.upper(root.get(Email_.from)), "%" + value.toUpperCase() + "%"));
//				p = cb.or(p, cb.like(cb.upper(root.get(Email_.to)), "%" + value.toUpperCase() + "%"));
//				p = cb.or(p, cb.like(cb.upper(root.get(Email_.bcc)), "%" + value.toUpperCase() + "%"));
//				p = cb.or(p, cb.like(cb.upper(root.get(Email_.subject)), "%" + value.toUpperCase() + "%"));
//				Join<Email, User> joinUser = root.join(Email_.user, JoinType.INNER);
//				p = cb.or(p, cb.like(cb.upper(joinUser.get(User_.guid)), "%" + value.toUpperCase() + "%"));
//				return p;
//			}
//		};
//	}
	
	public static Specification<Email> sentDateIsNull() {
		return new Specification<Email>() {
			@Override
			public Predicate toPredicate(Root<Email> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.isNull(root.get(Email_.sentDate));
				return p;
			}
		};
	}
	
	public static Specification<Email> failedFalse() {
		return new Specification<Email>() {
			@Override
			public Predicate toPredicate(Root<Email> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.isFalse(root.get(Email_.failed));
				return p;
			}
		};
	}
	
	public static Specification<Email> user(User user) {
		return new Specification<Email>() {
			@Override
			public Predicate toPredicate(Root<Email> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(Email_.user), user);
				return p;
			}
		};
	}
	
	public static Specification<Email> createdDateStart(Date dateStart) {
		return new Specification<Email>() {
			@Override
			public Predicate toPredicate(Root<Email> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(Email_.createdDate), dateStart);
				return p;
			}
		};
	}

	public static Specification<Email> createdDateEnd(Date dateEnd) {
		return new Specification<Email>() {
			@Override
			public Predicate toPredicate(Root<Email> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(Email_.createdDate), dateEnd);
				return p;
			}
		};
	}

	public static Specification<Email> emailTemplate(EmailTemplate emailTemplate) {
		return new Specification<Email>() {
			@Override
			public Predicate toPredicate(Root<Email> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(Email_.template), emailTemplate);
				return p;
			}
		};
	}

}