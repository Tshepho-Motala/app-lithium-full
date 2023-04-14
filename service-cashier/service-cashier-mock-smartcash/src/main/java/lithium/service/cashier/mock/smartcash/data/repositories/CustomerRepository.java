package lithium.service.cashier.mock.smartcash.data.repositories;

import lithium.service.cashier.mock.smartcash.data.entities.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
    Customer findByMsisdn(String msisdn);
}
