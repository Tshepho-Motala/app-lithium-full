package lithium.service.cashier.mock.inpay.data.repositories;

import lithium.service.cashier.mock.inpay.data.entities.InpayTransaction;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InpayTransactionRepository extends PagingAndSortingRepository<InpayTransaction, Long> {
    InpayTransaction findTransactionByInpayUniqueReference(String inpayUniqueReference);
    InpayTransaction findByxRequestId(String xRequestId);
}
