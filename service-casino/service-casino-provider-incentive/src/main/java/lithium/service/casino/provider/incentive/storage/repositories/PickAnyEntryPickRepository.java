package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyEntry;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyEntryPick;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PickAnyEntryPickRepository extends PagingAndSortingRepository<PickAnyEntryPick, Long> {
    PickAnyEntryPick findByEntryAndIncentiveEventId(PickAnyEntry entry, long incentiveEventId);
}
