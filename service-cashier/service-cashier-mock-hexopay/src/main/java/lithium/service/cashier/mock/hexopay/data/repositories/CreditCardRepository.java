package lithium.service.cashier.mock.hexopay.data.repositories;

import lithium.service.cashier.mock.hexopay.data.entities.CreditCard;
import lithium.service.cashier.mock.hexopay.data.entities.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


public interface CreditCardRepository extends PagingAndSortingRepository<CreditCard, Long> {
    List<CreditCard> findByStamp(String stamp);
    CreditCard findByCustomerAndStamp(Customer customer, String stamp);
    CreditCard findByToken(String token);
    List<CreditCard> findByCustomer(Customer customer);
}
