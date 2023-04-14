package lithium.service.user.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import lithium.service.user.data.entities.Domain;

@RepositoryRestResource(collectionResourceRel = "domains", path = "domains")
public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	Domain findByName(String name);
}