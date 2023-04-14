package lithium.service.user.search.data.repositories.cashier;

import lithium.service.cashier.data.entities.TransactionStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("cashier.TransactionStatusRepository")
public interface TransactionStatusRepository extends PagingAndSortingRepository<TransactionStatus, Long> {

  TransactionStatus findByCode(String code);
}
