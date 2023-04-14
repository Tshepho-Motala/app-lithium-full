package lithium.service.casino.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.AutoBonusAllocation;
import lithium.service.casino.data.entities.Domain;

public interface AutoBonusAllocationRepository extends PagingAndSortingRepository<AutoBonusAllocation, Long>, JpaSpecificationExecutor<AutoBonusAllocation> {
	AutoBonusAllocation findByDomainAndToken(Domain domain, String token);
}
