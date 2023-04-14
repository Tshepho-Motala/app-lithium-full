package lithium.service.notifications.data.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.notifications.data.entities.Domain;
import lithium.service.notifications.data.entities.Domain_;
import lithium.service.notifications.data.entities.Notification;
import lithium.service.notifications.data.entities.Notification_;

public class NotificationSpecifications {
	public static Specification<Notification> any(String search) {
		return new Specification<Notification>() {
			@Override
			public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(
					cb.like(cb.upper(root.get(Notification_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Notification_.displayName)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Notification_.description)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<Notification> domains(List<String> domains) {
		return new Specification<Notification>() {
			@Override
			public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Notification, Domain> domainJoin = root.join(Notification_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}
}
