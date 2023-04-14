package lithium.service.games.client.service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.GamesInternalSystemClient;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.GameType;
import lithium.service.games.client.objects.MultiGameUnlockRequest;
import lithium.service.games.client.objects.SimpleGameUserStatus;
import lithium.service.games.client.system.GameTypeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GamesInternalClientService {
    @Autowired
    private LithiumServiceClientFactory serviceClientFactory;

    public List<Game> getGamesForDomainAndProvider(String domainName, String provider) {
        return getGamesInternalClient().getGamesForDomainAndProvider(domainName, provider);
    }

    public List<Game> getGamesForDomain(String domainName) throws Exception {
        return (List<Game>) getGamesClient().listDomainGames(domainName)
                .getData();
    }

    public List<SimpleGameUserStatus> unlockGamesForUser(String domainName, String userGuid, List<String> gameGuids) {
        return getGamesInternalClient().unlockGamesForUser(domainName, MultiGameUnlockRequest.builder()
                        .gameGuids(gameGuids)
                        .userGuid(userGuid)
                .build());
    }

    public List<GameType> getGameTypesForDomain(String domainName) {
        return getGameTypeClient().getGameTypesForDomain(domainName);
    }
    public GamesInternalSystemClient getGamesInternalClient() {
        GamesInternalSystemClient client = null;
        try {
            return serviceClientFactory.target(GamesInternalSystemClient.class, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Failed to initialize GamesInternalClient", e);
        }
        return client;
    }

    public GameTypeClient getGameTypeClient() {
        GameTypeClient client = null;
        try {
            return serviceClientFactory.target(GameTypeClient.class, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Failed to initialize GameTypeClient", e);
        }
        return client;
    }

    public GamesClient getGamesClient() {
        GamesClient client = null;
        try {
            return serviceClientFactory.target(GamesClient.class, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Failed to initialize GameTypeClient", e);
        }
        return client;
    }
}
