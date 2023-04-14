package lithium.service.user.search.data.repositories.cashier;

import java.util.List;
import lithium.service.cashier.data.entities.Transaction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("cashier.TransactionRepository")
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

  List<Transaction> findAll();
}
