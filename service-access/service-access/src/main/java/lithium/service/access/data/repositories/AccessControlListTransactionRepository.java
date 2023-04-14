package lithium.service.access.data.repositories;

import lithium.service.access.data.entities.AccessControlListTransactionData;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AccessControlListTransactionRepository extends PagingAndSortingRepository<AccessControlListTransactionData, Long> {
}
