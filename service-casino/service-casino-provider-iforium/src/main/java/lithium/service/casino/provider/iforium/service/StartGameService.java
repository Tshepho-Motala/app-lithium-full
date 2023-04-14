package lithium.service.casino.provider.iforium.service;

import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;

import java.io.UnsupportedEncodingException;

public interface StartGameService {
    String startGame(String token, String gameId, String domainName, boolean isDemoGame, String deviceChannel, String lang) throws UnsupportedEncodingException, Status550ServiceDomainClientException, Status512ProviderNotConfiguredException, Status411UserNotFoundException, LithiumServiceClientFactoryException, UserClientServiceFactoryException, UserNotFoundException;
}
