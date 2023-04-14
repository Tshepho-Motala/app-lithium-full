package lithium.service.event.repositories;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.event.entities.Domain;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	
	public Domain findByName(String domainName);

}