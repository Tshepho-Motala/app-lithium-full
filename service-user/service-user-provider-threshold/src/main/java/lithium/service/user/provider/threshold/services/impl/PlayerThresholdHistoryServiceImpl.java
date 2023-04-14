package lithium.service.user.provider.threshold.services.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.accounting.enums.Granularity;
import lithium.service.accounting.objects.Period;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.service.user.provider.threshold.data.repositories.PlayerThresholdHistoryRepository;
import lithium.service.user.provider.threshold.services.LimitService;
import lithium.service.user.provider.threshold.services.PlayerThresholdHistoryService;
import lithium.service.user.provider.threshold.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlayerThresholdHistoryServiceImpl implements PlayerThresholdHistoryService {

  @Autowired
  private PlayerThresholdHistoryRepository playerThresholdHistoryRepository;

  @Autowired
  private LimitService limitService;
  @Autowired
  private UserService userService;

  @Override
  public PlayerThresholdHistory save(
      PlayerThresholdHistory playerThresholdHistory) {
    return playerThresholdHistoryRepository.save(playerThresholdHistory);
  }

  @Override
  public Iterable<PlayerThresholdHistory> findAll() {
    return playerThresholdHistoryRepository.findAll();
  }

  @Override
  public Optional<PlayerThresholdHistory> findOne(Long id) {
    return playerThresholdHistoryRepository.findById(id);
  }

  @Override
  public PlayerThresholdHistory savePlayerThresholdHistory(ThresholdRevision thresholdRevision, Long netLossToHouseCents, Long debitCents, User user) {

    BigDecimal debitCentsConverted = CurrencyAmount.fromCents(debitCents.longValue()).toAmount();
    BigDecimal netLossToHouseConverted = CurrencyAmount.fromCents(netLossToHouseCents).toAmount();
    BigDecimal limitAmountConverted = limitService.getLimitAmount(thresholdRevision, user);

    PlayerThresholdHistory playerThresholdHistory = PlayerThresholdHistory.builder()
        .thresholdHitDate(new Date())
        .thresholdRevision(thresholdRevision)
        .amount(debitCentsConverted)
        .user(user)
        .build();

    if (thresholdRevision.getGranularity() == Granularity.GRANULARITY_DAY.id()) {
      playerThresholdHistory.setDailyLimit(limitAmountConverted);
      playerThresholdHistory.setDailyLimitUsed(netLossToHouseConverted);
    } else if (thresholdRevision.getGranularity() == Granularity.GRANULARITY_WEEK.id()) {
      playerThresholdHistory.setWeeklyLimit(limitAmountConverted);
      playerThresholdHistory.setWeeklyLimitUsed(netLossToHouseConverted);
    } else if (thresholdRevision.getGranularity() == Granularity.GRANULARITY_MONTH.id()) {
      playerThresholdHistory.setMonthlyLimit(limitAmountConverted);
      playerThresholdHistory.setMonthlyLimitUsed(netLossToHouseConverted);
    }
    return save(playerThresholdHistory);

  }

  @Override
  public List<PlayerThresholdHistory> findByUserGuid(String userGuid)
  throws Status500InternalServerErrorException
  {
    User user = userService.findOrCreate(userGuid);
    return playerThresholdHistoryRepository.findByUser(user);
  }

  @Override
  public List<PlayerThresholdHistory> findByUserAndThresholdRevisionAndPeriod(User user,
      ThresholdRevision thresholdRevision, Period period) {
    return playerThresholdHistoryRepository.findPlayerThresholdHistoriesByUserAndThresholdRevisionAndThresholdHitDateBetween(user, thresholdRevision,
        period.getDateStart(), period.getDateEnd());
  }

  @Override
  public Page<PlayerThresholdHistory> findByThresholdHitDateBetween(Date startDateTime, Date endDateTime,Pageable pageable) {
    return  playerThresholdHistoryRepository.findByThresholdHitDateBetween(startDateTime,endDateTime,pageable);
  }

  @Override
  public Page<PlayerThresholdHistory> findByThresholdHitDateGreaterThanEqualAndThresholdHitDateLessThanEqual(
      Date startDate, Date endDate, PageRequest pageRequest) {
    return playerThresholdHistoryRepository.findByThresholdHitDateGreaterThanEqualAndThresholdHitDateLessThanEqual
        (startDate,endDate,pageRequest);
  }

  @Override
  public Page<PlayerThresholdHistory> findAll(Specification<PlayerThresholdHistory> spec,
      PageRequest pageRequest) {
    return playerThresholdHistoryRepository.findAll(spec, pageRequest);
  }

}
