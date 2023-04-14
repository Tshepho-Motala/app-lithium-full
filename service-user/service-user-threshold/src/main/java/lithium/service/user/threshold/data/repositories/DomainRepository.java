package lithium.service.user.threshold.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.user.threshold.data.entities.Domain;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {

}
