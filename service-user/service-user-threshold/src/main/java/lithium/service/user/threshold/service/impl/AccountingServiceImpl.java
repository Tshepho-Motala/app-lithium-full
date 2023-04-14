package lithium.service.user.threshold.service.impl;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.client.objects.Granularity;
import lithium.service.user.threshold.service.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountingServiceImpl implements AccountingService {

  @Autowired
  private AccountingClientService accountingClientService;

  @Override
  public Long findWithdrawalAmountInCentsByUserAndCurrencyAndGranularity(String domainName, String ownerGuid, String currency,
      Granularity granularity)
  throws Status500InternalServerErrorException
  {
    try {
      if (granularity == Granularity.GRANULARITY_TOTAL) {
        return accountingClientService.findLifetimeWithdrawalsAmountInCentsByUserAndGranularityAndCurrency(domainName, ownerGuid,
            granularity.granularity(), currency);
      }
      return accountingClientService.findLastAmountInCentsByUserAndAccountCodeAndTransactionTypeAndGranularityAndCurrency(domainName, ownerGuid,
          AccountingClientService.ACCOUNT_CODE_CASHIER_PAYOUT, AccountingClientService.TRAN_TYPE_WITHDRAWAL, granularity.granularity(), currency);
    } catch (Exception e) {
      throw new Status500InternalServerErrorException("Could not retrieve last withdrawal for: " + ownerGuid + " for " + granularity, e);
    }
  }

  @Override
  public Long findDepositAmountInCentsByUserAndCurrencyAndGranularity(String domainName, String ownerGuid, String currency, Granularity granularity)
  throws Status500InternalServerErrorException
  {
    try {
      if (granularity == Granularity.GRANULARITY_TOTAL) {
        return accountingClientService.findLifetimeDepositAmountInCentsByUserAndGranularityAndCurrency(domainName, ownerGuid,
            granularity.granularity(), currency);
      }
      return (accountingClientService.findLastAmountInCentsByUserAndAccountCodeAndTransactionTypeAndGranularityAndCurrency(domainName, ownerGuid,
          AccountingClientService.ACCOUNT_CODE_PLAYER_CASHIER_DEPOSIT, AccountingClientService.TRAN_TYPE_DEPOSIT, granularity.granularity(), currency)
          * -1);
    } catch (Exception e) {
      throw new Status500InternalServerErrorException("Could not retrieve last deposit for: " + ownerGuid + " for " + granularity, e);
    }
  }
}
