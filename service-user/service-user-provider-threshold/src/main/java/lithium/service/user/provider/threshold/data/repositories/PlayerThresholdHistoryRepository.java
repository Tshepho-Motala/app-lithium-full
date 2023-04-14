package lithium.service.user.provider.threshold.data.repositories;

import java.util.Date;
import java.util.List;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerThresholdHistoryRepository extends PagingAndSortingRepository<PlayerThresholdHistory, Long>,
    JpaSpecificationExecutor<PlayerThresholdHistory> {
  List<PlayerThresholdHistory> findByUser(User user);
  List<PlayerThresholdHistory> findPlayerThresholdHistoriesByUserAndThresholdRevisionAndThresholdHitDateBetween(User user, ThresholdRevision thresholdRevision, Date periodStart, Date periodEnd);
  Page<PlayerThresholdHistory> findByThresholdHitDateBetween(Date startDateTime,Date endDateTime,Pageable pageable);
  Page<PlayerThresholdHistory> findByThresholdHitDateGreaterThanEqualAndThresholdHitDateLessThanEqual(Date startDate, Date endDate,
      Pageable pageRequest);
}
