package lithium.service.access.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.access.data.entities.ExternalList;

public interface ExternalListRepository extends PagingAndSortingRepository<ExternalList, Long>, JpaSpecificationExecutor<ExternalList> {
}
