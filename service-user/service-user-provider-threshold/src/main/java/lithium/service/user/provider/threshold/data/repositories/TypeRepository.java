package lithium.service.user.provider.threshold.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.user.provider.threshold.data.entities.Type;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends FindOrCreateByNameRepository<Type, Long> {
}
