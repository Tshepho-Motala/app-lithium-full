package lithium.service.casino.provider.slotapi.storage.repositories;

import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.entities.BetResult;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BetResultRepository extends PagingAndSortingRepository<BetResult, Long> {
    BetResult findByBetResultTransactionId(String betResultTransactionId);
}