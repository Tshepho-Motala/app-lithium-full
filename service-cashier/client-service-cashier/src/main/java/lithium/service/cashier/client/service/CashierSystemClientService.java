package lithium.service.cashier.client.service;

import lithium.service.Response;
import lithium.service.cashier.client.CashierSystemClient;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CashierSystemClientService {
    @Autowired @Setter
    LithiumServiceClientFactory services;

    public Long cancelPendingWithdrawals(String domainName, String userGuid, String comment) throws Exception {

        CashierSystemClient cashierSystemClient = services.target(CashierSystemClient.class);

        Response<Long> cancelResponse = cashierSystemClient.cancelAllPendingWithdrawals(domainName, userGuid, comment);

        if (!cancelResponse.isSuccessful()) {
            throw new Exception("Failed to cancel pending withdrawals for user: " + userGuid);
        }

        return cancelResponse.getData();
    }
}
