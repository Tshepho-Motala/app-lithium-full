package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.BetSelection;
import lithium.service.casino.provider.incentive.storage.entities.Event;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BetSelectionRepository extends PagingAndSortingRepository<BetSelection, Long> {

    List<BetSelection> findByBet(Bet bet);
    BetSelection findBySelectionGuid(String guid);

}