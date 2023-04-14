package lithium.service.user.threshold.data.repositories;

import java.util.Date;
import java.util.List;
import lithium.service.user.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.threshold.data.entities.ThresholdRevision;
import lithium.service.user.threshold.data.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerThresholdHistoryRepository extends PagingAndSortingRepository<PlayerThresholdHistory, Long>,
    JpaSpecificationExecutor<PlayerThresholdHistory> {

  List<PlayerThresholdHistory> findByUserAndThresholdRevisionAndThresholdHitDateBetween(User user, ThresholdRevision thresholdRevision,
      Date periodStart, Date periodEnd);
}
