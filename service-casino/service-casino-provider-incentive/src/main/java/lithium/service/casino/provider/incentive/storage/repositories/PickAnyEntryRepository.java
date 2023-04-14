package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyEntry;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PickAnyEntryRepository extends PagingAndSortingRepository<PickAnyEntry, Long> {
    PickAnyEntry findByEntryTransactionId(String entryTransactionId);
}
