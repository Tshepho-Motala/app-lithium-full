package lithium.service.games.provider.google.rge.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.games.provider.google.rge.data.entities.Domain;

public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {
}
