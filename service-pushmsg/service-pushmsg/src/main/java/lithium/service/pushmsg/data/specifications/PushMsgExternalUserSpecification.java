package lithium.service.pushmsg.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.pushmsg.data.entities.Domain;
import lithium.service.pushmsg.data.entities.Domain_;
import lithium.service.pushmsg.data.entities.ExternalUser;
import lithium.service.pushmsg.data.entities.ExternalUser_;
import lithium.service.pushmsg.data.entities.User;
import lithium.service.pushmsg.data.entities.User_;

public class PushMsgExternalUserSpecification {
	public static Specification<ExternalUser> table(String domainName, String search) {
		return new Specification<ExternalUser>() {
			@Override
			public Predicate toPredicate(Root<ExternalUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<ExternalUser, User> joinUser = root.join(ExternalUser_.user, JoinType.INNER);
				Join<User, Domain> joinDomain = joinUser.join(User_.domain, JoinType.INNER);
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.like(cb.upper(joinUser.get(User_.guid)), "%" + search.toUpperCase() + "%"));
				return p;
			}
		};
	}
}