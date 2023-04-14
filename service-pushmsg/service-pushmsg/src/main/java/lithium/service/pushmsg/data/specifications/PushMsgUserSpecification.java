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

public class PushMsgUserSpecification {
	public static Specification<User> table(String domainName, String search) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<User, Domain> joinDomain = root.join(User_.domain, JoinType.INNER);
				
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.like(cb.upper(root.get(User_.guid)), "%" + search.toUpperCase() + "%"));
				return p;
			}
		};
	}
	
	public static Specification<ExternalUser> detailsTable(String guid, String search) {
		return new Specification<ExternalUser>() {
			@Override
			public Predicate toPredicate(Root<ExternalUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<ExternalUser, User> joinUser = root.join(ExternalUser_.user, JoinType.INNER);
				
				Predicate p = cb.equal(joinUser.get(User_.guid), guid);
				p = cb.and(p, cb.or(
					cb.like(cb.upper(root.get(ExternalUser_.deviceModel)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(ExternalUser_.ip)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(ExternalUser_.uuid)), "%" + search.toUpperCase() + "%")
				));
				return p;
			}
		};
	}
}