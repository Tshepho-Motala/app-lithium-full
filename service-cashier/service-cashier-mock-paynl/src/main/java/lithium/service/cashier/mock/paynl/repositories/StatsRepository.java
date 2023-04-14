package lithium.service.cashier.mock.paynl.repositories;

import lithium.service.cashier.mock.paynl.data.entities.Stats;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StatsRepository extends PagingAndSortingRepository<Stats, Long> {
}
