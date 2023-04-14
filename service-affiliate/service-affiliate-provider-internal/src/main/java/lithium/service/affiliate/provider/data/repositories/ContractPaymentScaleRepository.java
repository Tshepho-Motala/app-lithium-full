package lithium.service.affiliate.provider.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.Contract;
import lithium.service.affiliate.provider.data.entities.PaymentScale;

public interface ContractPaymentScaleRepository extends PagingAndSortingRepository<PaymentScale, Long> {

//	public List<PaymentScale> findByContract(Contract contract);
	
}