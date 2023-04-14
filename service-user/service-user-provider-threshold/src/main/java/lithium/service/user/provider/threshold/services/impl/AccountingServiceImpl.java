package lithium.service.user.provider.threshold.services.impl;

import java.util.Optional;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.client.AccountingPlayerClient;
import lithium.service.accounting.objects.Currency;
import lithium.service.accounting.objects.Period;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.service.user.provider.threshold.services.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountingServiceImpl implements AccountingService {

  private LithiumServiceClientFactory services;

  @Autowired
  public AccountingServiceImpl(LithiumServiceClientFactory services) {
    this.services = services;
  }

  @Override
  @TimeThisMethod
  public Long findNetLossToPlayer(User user, Period period, Currency currency) throws Exception {

    try {
      SW.start("findNetLossForPlayer " + period.getGranularity() + ":" + currency.getCode());
      AccountingPlayerClient playerClient = getAccountingPlayerClient().get();
      Long netLossToPlayer = playerClient.findNetLossToHouse(user.getDomain().getName(), period.getId(),
          currency.getCode(), user.getGuid()).getData();
      SW.stop();
      return netLossToPlayer;
    } catch (Exception e) {
      // Looking at findNetLossToHouse, this should really only fail whenever accounting is down as the exception thrown from PlayerService#netLossForPlayer is never thrown
      log.error("Failed to findNetLossToHouse for playerGuid: {}, periodId: {}, currencyCode: {} " +
          "| CompletedSummaryAccountTransactionTypeProcessor.processCompletedSummaryAccountTransactionType ", user.getGuid(), period.getId(), currency.getCode());
      throw e;
    }
  }

  private Optional<AccountingPlayerClient> getAccountingPlayerClient() {
    return getClient(AccountingPlayerClient.class, "service-accounting-provider-internal");
  }

  private <E> Optional<E> getClient(Class<E> theClass, String url) {
    E clientInstance = null;

    try {
      clientInstance = services.target(theClass, url, true);
    } catch (LithiumServiceClientFactoryException e) {
      log.error(e.getMessage(), e);
    }
    return Optional.ofNullable(clientInstance);
  }
}
