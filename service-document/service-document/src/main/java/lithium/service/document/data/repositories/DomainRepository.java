package lithium.service.document.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.document.data.entities.Domain;

public interface DomainRepository extends FindOrCreateByNameRepository<Domain, String> {
}