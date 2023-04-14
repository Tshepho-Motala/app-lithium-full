package lithium.service.notifications.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.notifications.data.entities.InboxMessagePlaceholderReplacement;

public interface InboxMessagePlaceholderReplacementRepository extends PagingAndSortingRepository<InboxMessagePlaceholderReplacement, Long>, JpaSpecificationExecutor<InboxMessagePlaceholderReplacement> {
}
