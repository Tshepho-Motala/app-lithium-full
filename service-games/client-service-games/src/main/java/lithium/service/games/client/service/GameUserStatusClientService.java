package lithium.service.games.client.service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.games.client.GameUserStatusClient;
import lithium.service.games.client.objects.GameUserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GameUserStatusClientService {

    @Autowired
    private LithiumServiceClientFactory services;

    private GameUserStatusClient getGameUserStatusServiceClient() throws LithiumServiceClientFactoryException {
        try {
            return services.target(GameUserStatusClient.class, "service-games", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public List<GameUserStatus> findUnlockedForUser(String userGuid) throws LithiumServiceClientFactoryException {
        return getGameUserStatusServiceClient().findUnlockedFreeGamesForUser(userGuid);
    }

    public void unlockAllFreeGames(String userGuid) throws LithiumServiceClientFactoryException {
        getGameUserStatusServiceClient().unlockFreeGames(userGuid);
    }

    public List<GameUserStatus> findGamesUserStatuses(String userGuid) throws LithiumServiceClientFactoryException {
        return getGameUserStatusServiceClient().findByUser(userGuid);
    }

}
