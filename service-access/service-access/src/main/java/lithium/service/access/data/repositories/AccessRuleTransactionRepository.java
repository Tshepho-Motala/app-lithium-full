package lithium.service.access.data.repositories;

import lithium.service.access.data.entities.AccessRuleTransaction;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AccessRuleTransactionRepository extends PagingAndSortingRepository<AccessRuleTransaction, Long> {
}
