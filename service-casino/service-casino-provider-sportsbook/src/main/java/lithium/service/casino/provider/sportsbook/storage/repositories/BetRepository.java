package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BetRepository extends PagingAndSortingRepository<Bet, Long> {
	Bet findByBetId(String betId);
	Bet findByRequestId(Long requestId);
	List<Bet> findByReservation(Reservation reservation);
}
