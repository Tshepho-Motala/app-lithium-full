package lithium.service.cashier.mock.hexopay.data.repositories;

import lithium.service.cashier.mock.hexopay.data.entities.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
    Customer findByEmail(String stamp);
}
