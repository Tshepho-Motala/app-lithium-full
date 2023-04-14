package lithium.service.notifications.data.specifications;

import lithium.service.notifications.data.entities.Channel_;
import lithium.service.notifications.data.entities.Domain;
import lithium.service.notifications.data.entities.Domain_;
import lithium.service.notifications.data.entities.Inbox;
import lithium.service.notifications.data.entities.Inbox_;
import lithium.service.notifications.data.entities.Notification;
import lithium.service.notifications.data.entities.NotificationChannel;
import lithium.service.notifications.data.entities.NotificationChannel_;
import lithium.service.notifications.data.entities.NotificationType;
import lithium.service.notifications.data.entities.NotificationType_;
import lithium.service.notifications.data.entities.Notification_;
import lithium.service.notifications.data.entities.User;
import lithium.service.notifications.data.entities.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public class InboxSpecifications {
	public static Specification<Inbox> any(String search) {
		return new Specification<Inbox>() {
			@Override
			public Predicate toPredicate(Root<Inbox> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Inbox, Domain> domainJoin = root.join(Inbox_.domain, JoinType.INNER);
				Join<Inbox, User> userJoin = root.join(Inbox_.user, JoinType.INNER);
				return cb.or(
					cb.like(cb.upper(domainJoin.get(Domain_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(userJoin.get(User_.guid)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Inbox_.createdDate).as(String.class)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Inbox_.sentDate).as(String.class)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Inbox_.read).as(String.class)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<Inbox> domains(List<String> domains) {
		return new Specification<Inbox>() {
			@Override
			public Predicate toPredicate(Root<Inbox> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Inbox, Domain> domainJoin = root.join(Inbox_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}
	
	public static Specification<Inbox> read(Boolean read) {
		return new Specification<Inbox>() {
			@Override
			public Predicate toPredicate(Root<Inbox> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(Inbox_.read), read);
				return p;
			}
		};
	}
	
	public static Specification<Inbox> user(String userGuid) {
		return new Specification<Inbox>() {
			@Override
			public Predicate toPredicate(Root<Inbox> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Inbox, User> userJoin = root.join(Inbox_.user, JoinType.INNER);
				Predicate p = cb.equal(cb.upper(userJoin.get(User_.guid)), userGuid.toUpperCase());
				return p;
			}
		};
	}

	public static Specification<Inbox> withChannels(List<String> channels) {
		return (root, query, cb) -> {
			Join<Inbox, Notification> inboxNotificationJoin = root.join(Inbox_.notification, JoinType.INNER);
			ListJoin<Notification, NotificationChannel> join = inboxNotificationJoin.joinList("channels", JoinType.INNER);
			return cb.and(join.get(NotificationChannel_.channel).get(Channel_.name).in(channels));
		};
	}

	public static Specification<Inbox> processing(Boolean processing) {
		return (root, query, cb) -> cb.equal(root.get(Inbox_.processing), processing);
	}

	public static Specification<Inbox> processed(Boolean processed) {
		return (root, query, cb) -> cb.equal(root.get(Inbox_.processed), processed);
	}

	public static Specification<Inbox> createdBefore(Date date) {
		return (root, query, cb) -> cb.lessThan(root.get(Inbox_.createdDate), date);
	}

	public static Specification<Inbox> cta(Boolean cta) {
		return (root, query, cb) -> cb.equal(root.get(Inbox_.cta), cta);
	}

	public static Specification<Inbox> withType(String type) {
		return (root, query, cb) -> {
			Join<Inbox, Notification> inboxNotificationJoin = root.join(Inbox_.notification, JoinType.INNER);
			Join<Notification, NotificationType> join = inboxNotificationJoin.join(Notification_.notificationType, JoinType.INNER);
			return cb.equal(join.get(NotificationType_.name), type);
		};
	}

	public static Specification<Inbox> user(User user) {
		return (root, query, cb) -> cb.equal(root.get(Inbox_.user), user);
	}
}
