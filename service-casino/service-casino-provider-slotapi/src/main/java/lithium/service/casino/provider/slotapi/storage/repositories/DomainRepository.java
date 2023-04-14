package lithium.service.casino.provider.slotapi.storage.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.casino.provider.slotapi.storage.entities.Domain;

public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {
}
