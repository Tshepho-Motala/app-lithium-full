package lithium.service.casino.provider.iforium.service.impl;

import lithium.service.casino.provider.iforium.model.request.AlertWalletCallbackNotificationRequest;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.model.response.Response;
import lithium.service.casino.provider.iforium.service.AlertsService;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertsServiceImpl implements AlertsService {

    @Override
    public Response alertWalletCallbackNotification(
            AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest) throws LithiumServiceClientFactoryException, Status550ServiceDomainClientException {

        log.info(alertWalletCallbackNotificationRequest.toString());
        return new Response(ErrorCodes.SUCCESS.getCode());
    }
}
