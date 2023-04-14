package lithium.service.notifications.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.notifications.data.entities.Channel;

public interface ChannelRepository extends PagingAndSortingRepository<Channel, Long>, JpaSpecificationExecutor<Channel> {
	Channel findByName(String name);
}
