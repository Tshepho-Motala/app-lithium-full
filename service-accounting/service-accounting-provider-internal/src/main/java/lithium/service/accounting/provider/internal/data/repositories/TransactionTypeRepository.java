package lithium.service.accounting.provider.internal.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.accounting.provider.internal.data.entities.TransactionType;

public interface TransactionTypeRepository extends PagingAndSortingRepository<TransactionType, Long> {
	
	TransactionType findByCode(String code);

	default TransactionType findOne(Long id) {
		return findById(id).orElse(null);
	}


}
