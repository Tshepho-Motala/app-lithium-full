package lithium.service.user.provider.threshold.services.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lithium.math.CurrencyAmount;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionTypeDetail;
import lithium.service.accounting.objects.Currency;
import lithium.service.accounting.objects.Period;
import lithium.service.client.objects.Granularity;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.objects.PlayerLimit;
import lithium.service.user.provider.threshold.data.entities.Domain;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.data.entities.Threshold;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.service.user.provider.threshold.data.enums.Type;
import lithium.service.user.provider.threshold.services.AccountingService;
import lithium.service.user.provider.threshold.services.DomainService;
import lithium.service.user.provider.threshold.services.LimitService;
import lithium.service.user.provider.threshold.services.NotificationService;
import lithium.service.user.provider.threshold.services.PlayerThresholdHistoryService;
import lithium.service.user.provider.threshold.services.ThresholdRevisionService;
import lithium.service.user.provider.threshold.services.ThresholdService;
import lithium.service.user.provider.threshold.services.UserService;
import lithium.service.user.provider.threshold.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class LimitServiceImpl implements LimitService {

  @Autowired
  private UserService userService;
  @Autowired
  private DomainService domainService;
  @Autowired
  ThresholdService thresholdService;
  @Autowired
  private ThresholdRevisionService thresholdRevisionService;
  @Autowired
  private AccountingService accountingService;

  @Autowired
  private NotificationService notificationService;
  @Autowired
  private PlayerThresholdHistoryService playerThresholdHistoryService;
  @Autowired
  LimitInternalSystemService limitInternalSystemService;

  @Override
  public BigDecimal getLimitAmount(ThresholdRevision thresholdRevision, User user) {
    PlayerLimit playerLimit = limitInternalSystemService.findPlayerLossLimits(user.getGuid(), thresholdRevision.getGranularity());
    log.debug("Player loss limits for {} {}: {}", Granularity.fromGranularity(thresholdRevision.getGranularity()), user.getGuid(), playerLimit);
    BigDecimal percentageMultiplier = thresholdRevision.getPercentage().movePointLeft(2);
    if (playerLimit != null) {
      return CurrencyAmount.fromCents(playerLimit.getAmount()).toAmount().multiply(percentageMultiplier);
    }
    return thresholdRevision.getAmount().multiply(percentageMultiplier);
  }

  private int playerAge(User user)throws Exception{
    int year = user.getDobYear();
    int month = user.getDobMonth();
    int day = user.getDobDay();
    if(year>0 && month>0 && day>0) {
      Date playerDob = DateUtil.getDay(year, month, day);
      return DateUtil.getDiffYears(playerDob, new Date());
    }else {
      throw new Exception("Player age cannot be determined");
    }
  }

  @Override
  @Transactional
  public void processLossLimitEvent(String domainName, User user, CompleteSummaryAccountTransactionTypeDetail d)
      throws Exception {
    Domain domain = domainService.findOrCreate(domainName);
    Threshold threshold = null;
    int playerAge = playerAge(user);

    Optional<Threshold> domainBasedThreshold = thresholdService.findCurrentDomainThreshold(d.getGranularity(), domain.getName(),
        Type.LIMIT_TYPE_LOSS);
    Optional<Threshold> ageBasedThreshold = thresholdService.findCurrentAgeBasedThreshold(domain.getName(), d.getGranularity(),
        Type.LIMIT_TYPE_LOSS.typeName(), playerAge);
    if (ageBasedThreshold.isEmpty() && domainBasedThreshold.isEmpty()) {
      return;
    }
    if (ageBasedThreshold.isPresent()) {
      threshold = ageBasedThreshold.get();
    } else {
      threshold = domainBasedThreshold.get();
    }

    Period period = d.getSummaryAccountTransactionType().getPeriod();
    Currency currency = d.getSummaryAccountTransactionType().getAccount().getCurrency();
    Long netLossToPlayer = accountingService.findNetLossToPlayer(user, period, currency);
    if (netLossToPlayer == null) {
      throw new Exception("Net Loss to Player is null");
    }
    d.setNetLossToHouse(netLossToPlayer);
    boolean playerReachedThresholdLimit = thresholdRevisionService.playerReachedThresholdLimit(threshold.getCurrent(), d, user);
    if (playerReachedThresholdLimit) {
      Long netLossToHouse = d.getNetLossToHouse();
      Long debitCents = d.getSummaryAccountTransactionType().getDebitCents();
      log.debug("Player Reached Threshold Limit: " + user.getGuid() + " | Domain:" + period.getDomain() + " | Amount:" + debitCents + " | Period:"
          + period.getDateStart() + " - " + period.getDateEnd() + " | Granularity: " + period.getGranularity() + " | NetLossToPlayer: "
          + netLossToHouse);

      List<PlayerThresholdHistory> periodThresholdHistories = playerThresholdHistoryService.findByUserAndThresholdRevisionAndPeriod(user,
          threshold.getCurrent(), period);

      //If multiple breaches are triggered within the same period/granularity, only ONE notification to be sent to player. Next notification only happens in the next period for that granularity.
      if (periodThresholdHistories.isEmpty()) {
        // Only creating one player threshold history per period and granularity
        PlayerThresholdHistory playerThresholdHistory = playerThresholdHistoryService.savePlayerThresholdHistory(threshold.getCurrent(),
            netLossToHouse, debitCents, user);

        notificationService.sendMessageToPlayerInbox(threshold, user, playerThresholdHistory);
      }
      try {
        notificationService.sendMessageToExtremePush(threshold, user, debitCents);
      } catch (Exception e) {
        log.debug("An error occurred with extreme push API : {}", e.getMessage());
      }
    }
  }
}
