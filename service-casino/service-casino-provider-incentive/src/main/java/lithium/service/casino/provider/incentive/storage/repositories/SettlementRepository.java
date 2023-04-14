package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.service.casino.provider.incentive.storage.entities.Placement;
import lithium.service.casino.provider.incentive.storage.entities.Settlement;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SettlementRepository extends PagingAndSortingRepository<Settlement, Long> {
}