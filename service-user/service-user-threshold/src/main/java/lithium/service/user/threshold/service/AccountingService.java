package lithium.service.user.threshold.service;


import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.objects.Granularity;

public interface AccountingService {

  Long findWithdrawalAmountInCentsByUserAndCurrencyAndGranularity(String domainName, String ownerGuid, String currency, Granularity granularity)
  throws Status500InternalServerErrorException;

  Long findDepositAmountInCentsByUserAndCurrencyAndGranularity(String domainName, String ownerGuid, String currency, Granularity granularity)
  throws Status500InternalServerErrorException;
}
