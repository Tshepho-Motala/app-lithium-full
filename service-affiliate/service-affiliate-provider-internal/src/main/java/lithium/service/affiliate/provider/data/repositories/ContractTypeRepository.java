package lithium.service.affiliate.provider.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.ContractType;

public interface ContractTypeRepository extends PagingAndSortingRepository<ContractType, Long> {

	ContractType findByName(String name);
	
}