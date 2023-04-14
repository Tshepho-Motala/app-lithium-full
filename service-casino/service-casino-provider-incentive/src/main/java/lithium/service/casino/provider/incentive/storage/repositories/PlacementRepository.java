package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.service.casino.provider.incentive.storage.entities.Placement;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlacementRepository extends PagingAndSortingRepository<Placement, Long> {
}