package lithium.service.user.threshold.service;

import lithium.service.cashier.client.objects.transaction.dto.CashierClientTransactionDTO;

public interface CashierClientService {

  CashierClientTransactionDTO getLastDeposit(String userGuid);
}
