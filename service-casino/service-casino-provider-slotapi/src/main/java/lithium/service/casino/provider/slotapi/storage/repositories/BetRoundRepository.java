package lithium.service.casino.provider.slotapi.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.entities.BetRound;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BetRoundRepository extends FindOrCreateByGuidRepository<BetRound, Long>, JpaSpecificationExecutor<BetRound> {


}
