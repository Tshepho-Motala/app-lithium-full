package lithium.service.casino.provider.iforium.service;

import lithium.exceptions.ErrorCodeException;
import lithium.service.casino.provider.iforium.model.request.BalanceRequest;
import lithium.service.casino.provider.iforium.model.response.BalanceResponse;
import lithium.service.client.LithiumServiceClientFactoryException;

public interface BalanceService {

    BalanceResponse balance(BalanceRequest balanceRequest, String domainName) throws ErrorCodeException, LithiumServiceClientFactoryException;
}
