package lithium.service.user.threshold.consumer;

import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.client.AccountingStandardAccountCodes;
import lithium.service.accounting.objects.Account;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionType;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionTypeDetail;
import lithium.service.accounting.stream.ICompletedSummaryAccountTransactionTypeProcessor;
import lithium.service.client.objects.Granularity;
import lithium.service.user.threshold.config.ThresholdProperties;
import lithium.service.user.threshold.service.LimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CompletedSummaryAccountTransactionTypeProcessor implements ICompletedSummaryAccountTransactionTypeProcessor {

  @Autowired
  private LimitService limitService;
  @Autowired
  private ThresholdProperties thresholdProperties;

  @Override
  @TimeThisMethod
  @Retryable( maxAttempts = 10, backoff = @Backoff( delay = 10, maxDelay = 100, random = true ) )
  public void processCompletedSummaryAccountTransactionType(CompleteSummaryAccountTransactionType request)
  throws Exception
  {
    //For this code to execute you need to turn on send-completed-summary-account-transaction-type-event: true in the service-accounting-provider-internal application.yml file
    log.trace("CompleteSummaryAccountTransactionType: " + request + "TransactionType: " + request.getTransactionType() + "Created: "
        + request.getCreatedOn());
    for (CompleteSummaryAccountTransactionTypeDetail d: request.getDetails()) {
      Account account = d.getSummaryAccountTransactionType().getAccount();
      if ((!AccountingStandardAccountCodes.PLAYER_BALANCE_ACCOUNT.equalsIgnoreCase(account.getAccountType().getCode()))
          && (!AccountingStandardAccountCodes.PLAYER_BALANCE_ACCOUNT.equalsIgnoreCase(account.getAccountCode().getCode()))) {
        continue;
      }
      switch (Granularity.fromGranularity(d.getGranularity())) {
        case GRANULARITY_DAY:
          if (!thresholdProperties.isCalculateDayGranularityEnabled()) {
            continue;
          }
          break;
        case GRANULARITY_WEEK:
          if (!thresholdProperties.isCalculateWeekGranularityEnabled()) {
            continue;
          }
          break;
        case GRANULARITY_MONTH:
          if (!thresholdProperties.isCalculateMonthGranularityEnabled()) {
            continue;
          }
          break;
        case GRANULARITY_YEAR:
          if (!thresholdProperties.isCalculateYearGranularityEnabled()) {
            continue;
          }
          break;
        case GRANULARITY_TOTAL:
          if (!thresholdProperties.isCalculateTotalGranularityEnabled()) {
            continue;
          }
          break;
        default:
          continue;
      }

      log.debug("--------------------------------------------------");
      log.debug(
          "processCompletedSummaryAccountTransactionType Granularity: " + Granularity.fromGranularity(d.getGranularity()).name() + " | Account: "
              + d.getSummaryAccountTransactionType().getAccount().getOwner().getGuid() + ", AcctType: " + d.getSummaryAccountTransactionType()
              .getAccount()
              .getAccountType()
              .getCode() + ", AcctCode: " + d.getSummaryAccountTransactionType().getAccount().getAccountCode().getCode() + " | CreditCents: "
              + d.getSummaryAccountTransactionType().getCreditCents() + " | DebitCents: " + d.getSummaryAccountTransactionType().getDebitCents()
              + " | TranCount: " + d.getSummaryAccountTransactionType().getTranCount());

      if (request.getTransactionType().contains("BET")) {
        limitService.processLossLimitEvent(d);
      } else if (request.getTransactionType().contains("CASHIER")) {
        limitService.processCashierEvent(d);
      }
    }
  }
}