package lithium.service.notifications.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.notifications.data.entities.Notification;

public interface NotificationRepository extends PagingAndSortingRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
	Notification findByDomainNameAndName(String domainName, String name);
	List<Notification> findByDomainName(String domainName);

	default Notification findOne(Long id) {
		return findById(id).orElse(null);
	}
}
