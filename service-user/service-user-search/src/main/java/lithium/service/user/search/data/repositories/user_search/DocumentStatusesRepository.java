package lithium.service.user.search.data.repositories.user_search;

import lithium.service.user.search.data.entities.DocumentStatus;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DocumentStatusesRepository  extends PagingAndSortingRepository<DocumentStatus, Long>, JpaSpecificationExecutor<DocumentStatus> {
  DocumentStatus findById(long id);
}
