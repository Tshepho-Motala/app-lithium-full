package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.entities.ReservationCancel;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReservationCancelRepository extends PagingAndSortingRepository<ReservationCancel, Long> {
	ReservationCancel findByReservationReserveId(Long reserveId);
}
