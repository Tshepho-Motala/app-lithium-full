package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.Bet;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.data.entities.Provider;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface BetRepository extends PagingAndSortingRepository<Bet, Long>, JpaSpecificationExecutor<Bet> {
	Bet findByProviderAndBetTransactionId(Provider provider, String betTransactionId);

	List<Bet> findByBetRoundIn(List<BetRound> betRounds);

	Long deleteByBetRoundIn(List<BetRound> betRoundResultSet);
}
