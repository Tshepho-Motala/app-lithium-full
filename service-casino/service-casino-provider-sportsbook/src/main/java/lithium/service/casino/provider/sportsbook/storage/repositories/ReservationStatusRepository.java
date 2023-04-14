package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.casino.provider.sportsbook.storage.entities.ReservationStatus;

public interface ReservationStatusRepository extends FindOrCreateByNameRepository<ReservationStatus, Long> {
}
