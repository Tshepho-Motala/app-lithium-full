package lithium.service.stats.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.stats.data.entities.Event;
import lithium.service.stats.data.entities.Type;

public interface TypeRepository extends FindOrCreateByNameRepository<Type, Long> {
}
