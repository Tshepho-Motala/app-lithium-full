package lithium.service.games.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.games.data.entities.Domain;

public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {
}