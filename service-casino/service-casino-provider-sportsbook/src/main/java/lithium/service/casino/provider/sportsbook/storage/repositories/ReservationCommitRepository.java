package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.service.casino.provider.sportsbook.storage.entities.ReservationCommit;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReservationCommitRepository extends PagingAndSortingRepository<ReservationCommit, Long> {
	ReservationCommit findByReservationReserveId(Long reserveId);
}
