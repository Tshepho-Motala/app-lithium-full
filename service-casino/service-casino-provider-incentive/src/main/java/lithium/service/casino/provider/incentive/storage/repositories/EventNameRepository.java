package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.casino.provider.incentive.storage.entities.EventName;

public interface EventNameRepository extends FindOrCreateByNameRepository<EventName, Long> {
}
