package lithium.service.games.services;

import lithium.service.cashier.client.system.TransactionClient;
import lithium.service.cashier.client.objects.transaction.dto.CashierClientTransactionDTO;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CashierClientService {

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

    public CashierClientTransactionDTO getFirstDeposit(String userGuid) {
        return getTransactionClient().findFirstDeposit(userGuid);
    }

}
