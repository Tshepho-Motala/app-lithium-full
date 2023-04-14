package lithium.service.cashier.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Processor;

public interface ProcessorRepository extends PagingAndSortingRepository<Processor, Long> {
//	Processor findByNameAndUrlAndMethodId(String name, String url, Long methodId);
//	Processor findByNameAndUrlAndMethodNameAndMethodUrl(String name, String url, String methodName, String methodUrl);
//	List<Processor> findByMethodId(Long methodId);
//	List<Processor> findByMethodIdAndDepositTrue(Long methodId);
//	List<Processor> findByMethodIdAndWithdrawTrue(Long methodId);
	Processor findByCode(String code);

	default Processor findOne(Long id) {
		return findById(id).orElse(null);
	}
}
