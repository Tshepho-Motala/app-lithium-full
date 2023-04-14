package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.service.casino.provider.incentive.storage.entities.PickAnySettlement;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PickAnySettlementRepository extends PagingAndSortingRepository<PickAnySettlement, Long> {
    PickAnySettlement findBySettlementTransactionId(String settlementTransactionId);
}
