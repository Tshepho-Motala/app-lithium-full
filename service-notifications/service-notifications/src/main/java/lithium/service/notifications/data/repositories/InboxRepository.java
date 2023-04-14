package lithium.service.notifications.data.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.notifications.data.entities.Inbox;
import lithium.service.notifications.data.entities.Notification;
import lithium.service.notifications.data.entities.User;

public interface InboxRepository extends PagingAndSortingRepository<Inbox, Long>, JpaSpecificationExecutor<Inbox> {
	Inbox findByNotificationAndUser(Notification notification, User user);
	Page<Inbox> findByReadFalseAndProcessingFalseAndProcessedFalseAndCreatedDateBeforeOrderByCreatedDateDesc(Date createdDateBefore, Pageable pageable);
	List<Inbox> findByUser(User user);
    Inbox findOneByIdAndUser(Long id, User user);

	default Inbox findOne(Long id) {
		return findById(id).orElse(null);
	}

	@CacheEvict(value = "lithium.service.notifications.services.inbox-service.find-user-inbox", key = "#entity.user.guid")
	@Override
	<S extends  Inbox> S save(S entity);
}
