package lithium.service.settlement.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.settlement.data.entities.BatchSettlements;

public interface BatchSettlementsRepository extends PagingAndSortingRepository<BatchSettlements, Long>, JpaSpecificationExecutor<BatchSettlements> {
	BatchSettlements findByNameIgnoreCaseAndDomainName(String batchName, String domainName);
}
