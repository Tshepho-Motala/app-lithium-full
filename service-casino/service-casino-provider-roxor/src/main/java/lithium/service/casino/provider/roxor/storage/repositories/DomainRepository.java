package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.casino.provider.roxor.storage.entities.Domain;

public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {
}
