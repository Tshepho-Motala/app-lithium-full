package lithium.service.cashier.mock.inpay.data.repositories;

import lithium.service.cashier.mock.inpay.data.entities.InpayReason;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InpayReasonRepository extends PagingAndSortingRepository<InpayReason, Long> {
    public InpayReason findByCode(String message);

}
