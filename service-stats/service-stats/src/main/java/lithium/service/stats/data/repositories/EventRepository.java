package lithium.service.stats.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.stats.data.entities.Event;

public interface EventRepository extends FindOrCreateByNameRepository<Event, Long> {
}
