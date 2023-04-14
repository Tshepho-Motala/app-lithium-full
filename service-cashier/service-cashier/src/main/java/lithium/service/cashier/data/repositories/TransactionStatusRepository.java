package lithium.service.cashier.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.TransactionStatus;

import java.util.List;

public interface TransactionStatusRepository extends PagingAndSortingRepository<TransactionStatus, Long> {
	
	TransactionStatus findByCode(String code);
	List<TransactionStatus> findAllByCodeIn(List<String> codes);
}
