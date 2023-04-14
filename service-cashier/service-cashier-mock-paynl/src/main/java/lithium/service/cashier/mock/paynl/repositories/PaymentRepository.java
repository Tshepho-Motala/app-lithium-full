package lithium.service.cashier.mock.paynl.repositories;

import lithium.service.cashier.mock.paynl.data.entities.Payment;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long> {
    Optional<Payment> findByMethod(String method); 
}
