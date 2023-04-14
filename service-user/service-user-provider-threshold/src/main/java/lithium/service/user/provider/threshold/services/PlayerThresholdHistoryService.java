package lithium.service.user.provider.threshold.services;

import java.util.Date;
import java.util.List;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.accounting.objects.Period;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PlayerThresholdHistoryService extends AbstractService<PlayerThresholdHistory> {
  PlayerThresholdHistory savePlayerThresholdHistory(ThresholdRevision thresholdRevision, Long netLossToHouseCents, Long debitCents, User user);
  List<PlayerThresholdHistory> findByUserGuid(String userGuid)
  throws Status500InternalServerErrorException;
  List<PlayerThresholdHistory> findByUserAndThresholdRevisionAndPeriod(User user, ThresholdRevision thresholdRevision, Period period);
  Page<PlayerThresholdHistory> findByThresholdHitDateBetween(Date startDate, Date endDate,Pageable pageable);
  Page<PlayerThresholdHistory> findByThresholdHitDateGreaterThanEqualAndThresholdHitDateLessThanEqual(Date startDate, Date endDate,
      PageRequest pageRequest);

  Page<PlayerThresholdHistory> findAll(Specification<PlayerThresholdHistory> spec, PageRequest pageRequest);
}
