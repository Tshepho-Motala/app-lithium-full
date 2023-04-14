package lithium.service.user.threshold.service.impl;

import java.math.BigDecimal;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionTypeDetail;
import lithium.service.accounting.objects.Period;
import lithium.service.cashier.client.objects.transaction.dto.CashierClientTransactionDTO;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.objects.PlayerLimitV2Dto;
import lithium.service.user.threshold.client.enums.EType;
import lithium.service.user.threshold.data.context.ProcessingContext;
import lithium.service.user.threshold.data.entities.Domain;
import lithium.service.user.threshold.data.entities.Threshold;
import lithium.service.user.threshold.data.entities.User;
import lithium.service.user.threshold.service.AccountingService;
import lithium.service.user.threshold.service.CashierClientService;
import lithium.service.user.threshold.service.DomainService;
import lithium.service.user.threshold.service.LimitService;
import lithium.service.user.threshold.service.PlayerThresholdHistoryService;
import lithium.service.user.threshold.service.ThresholdService;
import lithium.service.user.threshold.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class LimitServiceImpl implements LimitService {

  @Autowired
  UserService userService;
  @Autowired
  DomainService domainService;
  @Autowired
  AccountingService accountingService;
  @Autowired
  ThresholdService thresholdService;
  @Autowired
  CachingDomainClientService cachingDomainClientService;
  @Autowired
  PlayerThresholdHistoryService playerThresholdHistoryService;
  @Autowired
  LimitInternalSystemService limitInternalSystemService;

  @Autowired
  private CashierClientService cashierClientService;

  //  @Override
  private PlayerLimitV2Dto getLossLimitsFromSvcLimit(Threshold threshold, User user)
  throws Status500InternalServerErrorException
  {
    PlayerLimitV2Dto playerLimit = limitInternalSystemService.findPlayerLimitV2WithNetLoss(user.getDomain().getName(), user.getGuid(),
        threshold.getGranularity().granularity(), EType.TYPE_LOSS_LIMIT.type());
    log.debug("Player loss limits for player({}), granularity: {}, type: {}: {}", user.getGuid(), threshold.getGranularity().name(),
        threshold.getType().getName(), playerLimit);
    if (playerLimit != null) {
      return playerLimit;
    }
    throw new Status500InternalServerErrorException("Could not retrieve player limit " + user.getGuid());
  }

  @Override
  @Transactional( rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW )
  public void processLossLimitEvent(CompleteSummaryAccountTransactionTypeDetail sattd)
  throws Status500InternalServerErrorException
  {
    log.debug("processLossLimitEvent: {}", sattd);
    ProcessingContext context = preProcess(sattd, EType.TYPE_LOSS_LIMIT);

    if (ObjectUtils.isEmpty(context)) {
      return;
    }
    PlayerLimitV2Dto limit = context.getLimit();
    BigDecimal netLossToHouse = limit.getNetLossAmount();
    boolean playerReachedLossLimitThreshold = (
        netLossToHouse.compareTo(limit.getLimitAmount().multiply(context.getThreshold().getCurrent().getPercentage().movePointLeft(2))) >= 0);

    if (playerReachedLossLimitThreshold) {
      log.info("Player ({}) reached loss limit threshold: period: " + context.getPeriod().getDateStart() + " - " + context.getPeriod().getDateEnd()
              + " | Granularity: {} | NetLossToPlayer: {}", context.getUser().getGuid(), context.getThreshold().getGranularity().name(),
          limit.getNetLossAmount());

      playerThresholdHistoryService.savePlayerThresholdHistory(context, limit, true);
    } else {
      log.debug("No loss limit threshold reached for player ({}) | Granularity: {}", context.getUser().getGuid(),
          context.getThreshold().getGranularity().name());
    }
  }

  @Override
  @Transactional( rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW )
  public void processCashierEvent(CompleteSummaryAccountTransactionTypeDetail sattd)
  throws Status500InternalServerErrorException
  {
    log.debug("processCashierEvent: {}", sattd);
    ProcessingContext context = preProcess(sattd, EType.TYPE_DEPOSIT_LIMIT);

    if (ObjectUtils.isEmpty(context)) {
      return;
    }
    PlayerLimitV2Dto limit = context.getLimit();

    CashierClientTransactionDTO lastDeposit = cashierClientService.getLastDeposit(context.getUser().getGuid());
    BigDecimal depositAmount = CurrencyAmount.fromCents(lastDeposit.getAmountCents()).toAmount();

    //    BigDecimal depositAmount = CurrencyAmount.fromCents(sattd.getSummaryAccountTransactionType().getCreditCents()).toAmount();

    boolean playerReachedDepositThreshold = (depositAmount.compareTo(context.getThreshold().getCurrent().getAmount()) >= 0);

    if (playerReachedDepositThreshold) {
      log.info("Player Reached Deposit Threshold: " + context.getUser().getGuid() + " | Domain:" + context.getDomain().getName() + " | Period:"
          + context.getPeriod().getDateStart() + " - " + context.getPeriod().getDateEnd() + " | Granularity: " + context.getPeriod().getGranularity()
          + " | NetLossToPlayer: " + limit.getNetLossAmount());

      BigDecimal depositAmount2 = CurrencyAmount.fromCentsAllowNull(
          accountingService.findDepositAmountInCentsByUserAndCurrencyAndGranularity(context.getDomain().getName(), context.getUser().getGuid(),
              context.getDefaultDomainCurrencySymbol(), context.getThreshold().getGranularity())).toAmount();
      BigDecimal withdrawalAmount = CurrencyAmount.fromCentsAllowNull(
          accountingService.findWithdrawalAmountInCentsByUserAndCurrencyAndGranularity(context.getDomain().getName(), context.getUser().getGuid(),
              context.getDefaultDomainCurrencySymbol(), context.getThreshold().getGranularity())).toAmount();
      BigDecimal netLifetimeDepositAmount = CurrencyAmount.fromCentsAllowNull(
          accountingService.findDepositAmountInCentsByUserAndCurrencyAndGranularity(context.getDomain().getName(), context.getUser().getGuid(),
              context.getDefaultDomainCurrencySymbol(), Granularity.GRANULARITY_TOTAL)).toAmount();

      playerThresholdHistoryService.savePlayerThresholdHistory(context, limit, depositAmount, depositAmount2, withdrawalAmount,
          netLifetimeDepositAmount, false);
    }
  }

  private ProcessingContext preProcess(CompleteSummaryAccountTransactionTypeDetail summaryAccountTransactionTypeDetail, EType type)
  throws Status500InternalServerErrorException
  {
    Domain domain = domainService.findOrCreate(
        summaryAccountTransactionTypeDetail.getSummaryAccountTransactionType().getAccount().getDomain().getName());
    if (domain == null) {
      throw new Status500InternalServerErrorException(
          "Unable to create new domain: " + summaryAccountTransactionTypeDetail.getSummaryAccountTransactionType()
              .getAccount()
              .getDomain()
              .getName());
    }
    String defaultDomainCurrencySymbol = cachingDomainClientService.getDefaultDomainCurrency(domain.getName());

    User user = userService.findOrCreate(summaryAccountTransactionTypeDetail.getSummaryAccountTransactionType().getAccount().getOwner().getGuid(),
        domain);

    user = userService.updateUser(user,
        summaryAccountTransactionTypeDetail.getSummaryAccountTransactionType().getAccount().getOwner().isTestAccount());

    int playerAge = userService.playerAge(user);
    Threshold threshold = thresholdService.findByAgeOrDefault(domain.getName(), type, summaryAccountTransactionTypeDetail.getGranularity(),
        playerAge);

    if (ObjectUtils.isEmpty(threshold)) {
      log.trace("No configured thresholds found. Ignoring. [{} - {} - {} - {}]", domain.getName(), type.name(),
          summaryAccountTransactionTypeDetail.getGranularity(), playerAge);
      return null;
    }
    log.debug("Threshold Found: {}", threshold);

    Period period = summaryAccountTransactionTypeDetail.getSummaryAccountTransactionType().getPeriod();
    PlayerLimitV2Dto limit = getLossLimitsFromSvcLimit(threshold, user);

    return new ProcessingContext(domain, defaultDomainCurrencySymbol, user, playerAge, threshold, period, limit, null, null);
  }
}
