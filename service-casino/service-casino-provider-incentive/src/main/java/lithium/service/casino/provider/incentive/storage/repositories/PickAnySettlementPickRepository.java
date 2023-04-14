package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.PickAnySettlementPick;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PickAnySettlementPickRepository extends PagingAndSortingRepository<PickAnySettlementPick, Long> {
}
