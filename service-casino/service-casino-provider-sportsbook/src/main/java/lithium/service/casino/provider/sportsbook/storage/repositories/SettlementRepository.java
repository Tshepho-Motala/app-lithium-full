package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.service.casino.provider.sportsbook.storage.entities.Settlement;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementCredit;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SettlementRepository extends PagingAndSortingRepository<Settlement, Long> {
	Settlement findByRequestId(Long requestId);
}
