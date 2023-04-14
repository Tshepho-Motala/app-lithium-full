package lithium.service.user.provider.threshold.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.objects.Account;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionType;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionTypeDetail;
import lithium.service.accounting.objects.Currency;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.accounting.stream.ICompletedSummaryAccountTransactionTypeProcessor;
import lithium.service.client.objects.Granularity;
import lithium.service.user.provider.threshold.config.Properties;
import lithium.service.user.provider.threshold.data.entities.Domain;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.data.entities.Threshold;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.service.user.provider.threshold.data.enums.Type;
import lithium.service.user.provider.threshold.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class CompletedSummaryAccountTransactionTypeProcessor implements ICompletedSummaryAccountTransactionTypeProcessor {

  @Autowired
  private LimitService limitService;

  @Autowired
  private UserService userService;
  @Autowired
  private AccountingService accountingService;
  @Autowired
  private Properties properties;
  @Autowired
  private ThresholdAgeGroupService thresholdAgeGroupService;

  private String domainName(CompleteSummaryAccountTransactionType request) {
    Optional<CompleteSummaryAccountTransactionTypeDetail> detail = request.getDetails().stream().findFirst();
    CompleteSummaryAccountTransactionTypeDetail completeSummary = detail.get();
    SummaryAccountTransactionType summaryAccountTransactionType = completeSummary.getSummaryAccountTransactionType();
    Account account = summaryAccountTransactionType.getAccount();
    lithium.service.accounting.objects.Domain domain = account.getDomain();
    return domain.getName();
  }

  private User user(CompleteSummaryAccountTransactionType completedSummaryAccountTransactionType)
  throws Status500InternalServerErrorException
  {
    Optional<CompleteSummaryAccountTransactionTypeDetail> detail = completedSummaryAccountTransactionType.getDetails().stream().findFirst();
    Account account = detail.get().getSummaryAccountTransactionType().getAccount();
    lithium.service.accounting.objects.User owner = account.getOwner();
    User user = userService.findOrCreate(owner.getGuid());
    if (!ObjectUtils.isEmpty(user)) {
      userService.updateUser(user, owner.isTestAccount());
    }
    return user;
  }

  @Override
  @TimeThisMethod
  @Retryable( maxAttempts = 10, backoff = @Backoff( delay = 10, maxDelay = 100, random = true ) )
  public void processCompletedSummaryAccountTransactionType(CompleteSummaryAccountTransactionType request)
  throws Exception
  {
    //For this code to execute you need to turn on send-completed-summary-account-transaction-type-event: true in the service-accounting-provider-internal application.yml file
    User user = user(request);
    log.trace("CompleteSummaryAccountTransactionType: " + request + "TransactionType: " + request.getTransactionType() + "Created: " + request.getCreatedOn());
    for (CompleteSummaryAccountTransactionTypeDetail d: request.getDetails()) {
      switch (lithium.service.accounting.enums.Granularity.fromId(d.getGranularity())) {
        case GRANULARITY_DAY:
          if (!properties.isCalculateDayGranularityEnabled()) {
            continue;
          }
          break;
        case GRANULARITY_WEEK:
          if (!properties.isCalculateWeekGranularityEnabled()) {
            continue;
          }
          break;
        case GRANULARITY_MONTH:
          if (!properties.isCalculateMonthGranularityEnabled()) {
            continue;
          }
          break;
        case GRANULARITY_YEAR:
          if (!properties.isCalculateYearGranularityEnabled()) {
            continue;
          }
          break;
        case GRANULARITY_TOTAL:
          if (!properties.isCalculateTotalGranularityEnabled()) {
            continue;
          }
          break;
        default:
          continue;
      }

      if (!Objects.equals(d.getSummaryAccountTransactionType().getAccount().getAccountCode().getCode(), AccountingService.PLAYER_BALANCE_TYPE_CODE_NAME)) {
        return;
      }

      log.debug("User: " + user + " Granularity: " + Granularity.fromGranularity(d.getGranularity()) + " | Account: " + d.getSummaryAccountTransactionType()
          .getAccount() + " | CreditCents: " + d.getSummaryAccountTransactionType().getCreditCents() + " | DebitCents: " + d.getSummaryAccountTransactionType().getDebitCents() + " | TranCount: " + d.getSummaryAccountTransactionType().getTranCount());

      if (request.getTransactionType().contains("BET")) {
        limitService.processLossLimitEvent(domainName(request), user, d);
      }
    }
  }
}