package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.entities.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import javax.persistence.LockModeType;

public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {
	Reservation findByReserveId(Long reserveId);
	Page<Reservation> findByReservationStatusOrderByAccountingLastRecheckedAsc(ReservationStatus reservationStatus, Pageable pageable);

	@Query("select o from #{#entityName} o where o.reserveId = :reserveId")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Reservation findByReserveIdAlwaysLock(@Param("reserveId") Long reserveId);
}
