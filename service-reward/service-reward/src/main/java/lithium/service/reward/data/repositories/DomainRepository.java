package lithium.service.reward.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.reward.data.entities.Domain;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainRepository extends FindOrCreateByNameRepository<Domain, Long> {

}