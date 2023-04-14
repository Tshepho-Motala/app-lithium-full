package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.TransactionPaymentType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionPaymentTypeRepository extends PagingAndSortingRepository<TransactionPaymentType, Long>, JpaSpecificationExecutor<TransactionPaymentType> {
	TransactionPaymentType findByPaymentType(String paymentType);
}
