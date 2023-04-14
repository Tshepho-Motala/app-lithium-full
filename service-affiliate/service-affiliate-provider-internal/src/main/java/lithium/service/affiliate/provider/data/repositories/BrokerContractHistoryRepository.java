package lithium.service.affiliate.provider.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.BrokerContractHistory;

public interface BrokerContractHistoryRepository extends PagingAndSortingRepository<BrokerContractHistory, Long> {
	
}