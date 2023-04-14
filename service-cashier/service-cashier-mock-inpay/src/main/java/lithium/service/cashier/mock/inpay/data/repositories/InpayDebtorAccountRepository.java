package lithium.service.cashier.mock.inpay.data.repositories;

import lithium.service.cashier.mock.inpay.data.entities.InpayDebtorAccount;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface InpayDebtorAccountRepository extends PagingAndSortingRepository<InpayDebtorAccount, Long> {
    Optional<InpayDebtorAccount> findBySchemeName(String schemeName);
}
