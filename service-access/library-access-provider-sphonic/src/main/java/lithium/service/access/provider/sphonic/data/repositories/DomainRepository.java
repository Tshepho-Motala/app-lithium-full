package lithium.service.access.provider.sphonic.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.access.provider.sphonic.data.entities.Domain;

public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {
}
