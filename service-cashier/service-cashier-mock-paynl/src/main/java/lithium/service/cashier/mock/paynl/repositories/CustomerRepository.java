package lithium.service.cashier.mock.paynl.repositories;


import lithium.service.cashier.mock.paynl.data.entities.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
    Optional<Customer> findByFirstNameAndLastName(String firstName, String lastName);
}
