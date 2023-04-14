package lithium.service.cashier.mock.paynl.repositories;

import lithium.service.cashier.mock.paynl.data.entities.Amount;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AmountRepository extends PagingAndSortingRepository<Amount, Long> {
}
