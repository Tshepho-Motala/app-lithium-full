package lithium.service.user.threshold.service.impl;

import lithium.service.cashier.client.objects.transaction.dto.CashierClientTransactionDTO;
import lithium.service.cashier.client.system.TransactionClient;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.threshold.service.CashierClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CashierClientServiceImpl implements CashierClientService {

  @Autowired
  private LithiumServiceClientFactory services;

  private TransactionClient getTransactionClient() {
    TransactionClient clientInstance = null;

    try {
      clientInstance = services.target(TransactionClient.class, "service-cashier", true);
    } catch (LithiumServiceClientFactoryException e) {
      log.error(e.getMessage(), e);
    }
    return clientInstance;
  }

  @Override
  public CashierClientTransactionDTO getLastDeposit(String userGuid) {
    return getTransactionClient().findLastDeposit(userGuid);
  }

}
