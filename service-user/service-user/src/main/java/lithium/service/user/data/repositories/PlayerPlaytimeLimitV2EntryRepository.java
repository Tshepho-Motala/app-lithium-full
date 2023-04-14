package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.Period;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV2Entry;
import lithium.service.user.data.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerPlaytimeLimitV2EntryRepository extends PagingAndSortingRepository<PlayerPlaytimeLimitV2Entry, Long>,
    JpaSpecificationExecutor<PlayerPlaytimeLimitV2Entry> {
  PlayerPlaytimeLimitV2Entry findByPeriodAndUser(Period period, User user);

}
