package lithium.service.affiliate.provider.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.Contract;
import lithium.service.affiliate.provider.data.entities.ContractType;

public interface ContractRepository extends PagingAndSortingRepository<Contract, Long> {

	//public Contract findByTypeAndDefaultContractTrue(ContractType contractType);

	public List<Contract> findByTypeOrderByCreationDateDesc(ContractType contractType);

}