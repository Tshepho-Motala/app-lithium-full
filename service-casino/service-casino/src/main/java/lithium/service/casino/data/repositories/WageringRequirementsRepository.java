package lithium.service.casino.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.WageringRequirements;

public interface WageringRequirementsRepository extends PagingAndSortingRepository<WageringRequirements, Long> {
	
}