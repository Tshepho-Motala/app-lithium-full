package lithium.service.user.data.specifications;

import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.SignupEvent;
import lithium.service.user.data.entities.SignupEvent_;
import lithium.service.user.data.entities.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public class SignupEventSpecification {
	public static Specification<SignupEvent> anyContains(final String value) {
		return new Specification<SignupEvent>() {
			@Override
			public Predicate toPredicate(Root<SignupEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(SignupEvent_.ipAddress), value + "%");
				return p;
			}
		};
	}
	
	public static Specification<SignupEvent> domainIn(final List<Domain> domains) {
		return new Specification<SignupEvent>() {
			@Override
			public Predicate toPredicate(Root<SignupEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = root.get(SignupEvent_.domain).in(domains);
				return p;
			}
		};
	}

	public static Specification<SignupEvent> user(User user) {
		return new Specification<SignupEvent>() {
			@Override
			public Predicate toPredicate(Root<SignupEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(SignupEvent_.user), user);
				return p;
			}
		};
	}

	public static Specification<SignupEvent> signupDateRangeStart(Date signupDateRangeStart) {
		return new Specification<SignupEvent>() {
			@Override
			public Predicate toPredicate(Root<SignupEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(SignupEvent_.date).as(Date.class), signupDateRangeStart);
				return p;
			}
		};
	}

	public static Specification<SignupEvent> signupDateRangeEnd(Date signupDateRangeEnd) {
		return new Specification<SignupEvent>() {
			@Override
			public Predicate toPredicate(Root<SignupEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(SignupEvent_.date).as(Date.class), signupDateRangeEnd);
				return p;
			}
		};
	}

	public static Specification<SignupEvent> signupSuccessful(boolean successful) {
		return new Specification<SignupEvent>() {
			@Override
			public Predicate toPredicate(Root<SignupEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(SignupEvent_.successful), successful);
				return p;
			}
		};
	}
}