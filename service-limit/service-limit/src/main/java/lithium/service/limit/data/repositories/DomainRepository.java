package lithium.service.limit.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.limit.data.entities.Domain;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "domains", path = "domains")
public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {
    Domain findByName(String name);
}
