package lithium.service.pushmsg.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.pushmsg.data.entities.Domain;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	Domain findByName(String name);
}