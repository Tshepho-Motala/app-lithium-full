package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;

public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {
}
