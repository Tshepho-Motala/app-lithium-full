package lithium.service.access.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.access.data.entities.Domain;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	Domain findByName(String domainName);
}