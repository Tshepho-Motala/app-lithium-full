package lithium.service.casino.provider.iforium.service;

import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.model.request.CreateSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.request.RedeemSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.response.RedeemSessionTokenResponse;
import lithium.service.casino.provider.iforium.model.response.SessionTokenResponse;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;

public interface SessionService {

    String wrapSessionToken(String sessionKey);

    RedeemSessionTokenResponse redeemToken(RedeemSessionTokenRequest redeemSessionTokenRequest, String authorization, String xForwardedFor)
            throws LithiumServiceClientFactoryException, Status412LoginEventNotFoundException, Status550ServiceDomainClientException, Status512ProviderNotConfiguredException;

    SessionTokenResponse createToken(CreateSessionTokenRequest createSessionTokenRequest)
            throws LithiumServiceClientFactoryException, Status411UserNotFoundException;
}
