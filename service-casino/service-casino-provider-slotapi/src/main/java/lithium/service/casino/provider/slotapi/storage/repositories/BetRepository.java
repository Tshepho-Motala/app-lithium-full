package lithium.service.casino.provider.slotapi.storage.repositories;

import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 *
 */
public interface BetRepository extends PagingAndSortingRepository<Bet, Long>, JpaSpecificationExecutor<Bet> {
	Bet findByBetTransactionId(String betTransactionId);

	List<Bet> findByIdBetweenOrderByIdAsc(Long start, Long end);
}
