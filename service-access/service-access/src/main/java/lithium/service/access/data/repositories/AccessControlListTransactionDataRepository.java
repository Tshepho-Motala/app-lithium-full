package lithium.service.access.data.repositories;

import lithium.service.access.data.entities.AccessControlList;
import lithium.service.access.data.entities.AccessControlListTransactionData;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AccessControlListTransactionDataRepository extends PagingAndSortingRepository<AccessControlListTransactionData, Long> {
//	List<AccessControlListTransactionData> findByAccessControlList(AccessControlList accessControlList);
	Long countByAccessControlList(AccessControlList accessControlList);
}
