package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BetResult;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.data.entities.Provider;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BetResultRepository extends PagingAndSortingRepository<BetResult, Long> {
    BetResult findByProviderAndBetResultTransactionId(Provider provider, String betResultTransactionId);
    Long deleteByBetRoundIn(List<BetRound> betRoundResultSet);
    BetResult findByBetRoundId(Long id);
}