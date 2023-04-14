package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.TransactionTagTypeInfo;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TagTypeRepository extends PagingAndSortingRepository<TransactionTagTypeInfo, Integer> {
}
