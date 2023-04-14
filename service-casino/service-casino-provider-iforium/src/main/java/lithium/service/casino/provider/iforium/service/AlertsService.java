package lithium.service.casino.provider.iforium.service;

import lithium.service.casino.provider.iforium.model.request.AlertWalletCallbackNotificationRequest;
import lithium.service.casino.provider.iforium.model.response.Response;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;

public interface AlertsService {

    Response alertWalletCallbackNotification(
            AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest) throws LithiumServiceClientFactoryException, Status550ServiceDomainClientException;
}
