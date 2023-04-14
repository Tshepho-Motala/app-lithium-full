package lithium.service.kyc.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.kyc.entities.Domain;

public interface DomainRepository extends FindOrCreateByNameRepository<Domain, String> {
}
