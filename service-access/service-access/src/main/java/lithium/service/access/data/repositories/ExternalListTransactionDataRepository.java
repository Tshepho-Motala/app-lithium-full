package lithium.service.access.data.repositories;

import lithium.service.access.data.entities.ExternalList;
import lithium.service.access.data.entities.ExternalListTransactionData;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExternalListTransactionDataRepository extends PagingAndSortingRepository<ExternalListTransactionData, Long> {
//	List<ExternalListTransactionData> findByExternalList(ExternalList externalList);
	Long countByExternalList(ExternalList externalList);
}
