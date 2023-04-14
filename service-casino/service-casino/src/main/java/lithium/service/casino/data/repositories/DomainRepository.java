package lithium.service.casino.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.casino.data.entities.Domain;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {
}