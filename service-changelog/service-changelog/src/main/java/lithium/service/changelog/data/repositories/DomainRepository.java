package lithium.service.changelog.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.changelog.data.entities.Domain;

import java.util.List;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	
	Domain findByName(String name);
    List<Domain> findAllByNameIn(String[] domainNames);
}
