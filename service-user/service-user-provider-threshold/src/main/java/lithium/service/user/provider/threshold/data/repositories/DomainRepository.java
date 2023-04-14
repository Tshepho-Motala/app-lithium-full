package lithium.service.user.provider.threshold.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.user.provider.threshold.data.entities.Domain;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {
}
