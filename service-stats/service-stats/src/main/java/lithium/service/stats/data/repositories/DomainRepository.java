package lithium.service.stats.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.stats.data.entities.Domain;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	Domain findByName(String name);
}