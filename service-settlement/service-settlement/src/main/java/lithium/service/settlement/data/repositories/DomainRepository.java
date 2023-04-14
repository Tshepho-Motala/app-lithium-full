package lithium.service.settlement.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import lithium.service.settlement.data.entities.Domain;

@RepositoryRestResource(collectionResourceRel = "domains", path = "domains")
public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	Domain findByName(String name);
}