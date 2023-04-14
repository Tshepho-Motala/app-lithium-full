package lithium.service.user.threshold.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.user.threshold.data.entities.Type;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends FindOrCreateByNameRepository<Type, Long> {

}
