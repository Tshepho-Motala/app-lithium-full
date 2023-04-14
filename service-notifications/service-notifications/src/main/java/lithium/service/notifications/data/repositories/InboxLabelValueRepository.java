package lithium.service.notifications.data.repositories;

import lithium.service.notifications.data.entities.InboxLabelValue;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InboxLabelValueRepository extends PagingAndSortingRepository<InboxLabelValue, Long> {

}