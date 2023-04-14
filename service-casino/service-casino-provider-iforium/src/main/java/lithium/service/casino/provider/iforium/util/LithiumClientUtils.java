package lithium.service.casino.provider.iforium.util;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.games.client.GamesClient;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class LithiumClientUtils {

    private final LithiumServiceClientFactory lithiumServiceClientFactory;

    public SystemLoginEventsClient getSystemLoginEventsClient() throws LithiumServiceClientFactoryException {
        return lithiumServiceClientFactory.target(SystemLoginEventsClient.class, "service-user", true);
    }

    public GamesClient getGamesClient() throws LithiumServiceClientFactoryException {
        return lithiumServiceClientFactory.target(GamesClient.class, "service-games", true);
    }
}
