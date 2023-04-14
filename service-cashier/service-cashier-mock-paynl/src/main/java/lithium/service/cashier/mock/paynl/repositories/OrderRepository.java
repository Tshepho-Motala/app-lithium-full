package lithium.service.cashier.mock.paynl.repositories;

import lithium.service.cashier.mock.paynl.data.entities.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {
}
