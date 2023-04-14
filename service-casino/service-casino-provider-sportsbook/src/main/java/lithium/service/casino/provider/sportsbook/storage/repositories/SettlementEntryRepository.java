package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.service.casino.provider.sportsbook.storage.entities.SettlementEntry;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SettlementEntryRepository extends PagingAndSortingRepository<SettlementEntry, Long> {
}
