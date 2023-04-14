package lithium.service.user.data.specifications;

import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.LoginEvent;
import lithium.service.user.data.entities.LoginEvent_;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public class LoginEventSpecification {
	public static Specification<LoginEvent> user(final User user) {
		return new Specification<LoginEvent>() {
			@Override
			public Predicate toPredicate(Root<LoginEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(LoginEvent_.user), user);
				return p;
			}
		};
	}

	public static Specification<LoginEvent> anyContains(final String value) {
		return new Specification<LoginEvent>() {
			@Override
			public Predicate toPredicate(Root<LoginEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(LoginEvent_.date).as(String.class), value + "%");
				p = cb.or(p, cb.like(root.get(LoginEvent_.ipAddress), value + "%"));
				Join<LoginEvent, User> joinUser = root.join(LoginEvent_.user, JoinType.INNER);
				p = cb.or(p, cb.like(joinUser.get(User_.username), value + "%"));
				p = cb.or(p, cb.like(joinUser.get(User_.firstName), value + "%"));
				p = cb.or(p, cb.like(joinUser.get(User_.lastName), value + "%"));
				return p;
			}
		};
	}
	
	public static Specification<LoginEvent> domainInAndUserDomainIn(List<Domain> domains) {
		return new Specification<LoginEvent>() {
			@Override
			public Predicate toPredicate(Root<LoginEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<LoginEvent, User> joinUser = root.join(LoginEvent_.user, JoinType.LEFT);
				
				Predicate p = cb.or(
					joinUser.get(User_.domain).in(domains),
					root.get(LoginEvent_.domain).in(domains)
				);
				
				return p;
			}
		};
	}

    public static Specification<LoginEvent> domainIn(List<Domain> domains) {
        return new Specification<LoginEvent>() {
            @Override
            public Predicate toPredicate(Root<LoginEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate p = root.get(LoginEvent_.domain).in(domains);

                return p;
            }
        };
    }

    public static Specification<LoginEvent> loginDateRangeStart(Date loginDateRangeStart) {
        return new Specification<LoginEvent>() {
            @Override
            public Predicate toPredicate(Root<LoginEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate p = cb.greaterThanOrEqualTo(root.get(LoginEvent_.date).as(Date.class), loginDateRangeStart);
                return p;
            }
        };
    }

    public static Specification<LoginEvent> loginDateRangeEnd(Date loginDateRangeEnd) {
        return new Specification<LoginEvent>() {
            @Override
            public Predicate toPredicate(Root<LoginEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate p = cb.lessThanOrEqualTo(root.get(LoginEvent_.date).as(Date.class), loginDateRangeEnd);
                return p;
            }
        };
    }
}
