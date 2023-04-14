package lithium.service.games.client.service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.games.client.GamesInternalSystemClient;
import lithium.service.games.client.objects.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class GamesInternalSystemClientService {

    @Autowired
    private LithiumServiceClientFactory services;

    private GamesInternalSystemClient getGamesInternalSystemClient() throws LithiumServiceClientFactoryException {
        try {
            return services.target(GamesInternalSystemClient.class, "service-games", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public Response<List<Game>> getByGuidsAndDomain(String domainName, Set<String> guids) throws LithiumServiceClientFactoryException {
        return getGamesInternalSystemClient().getDomainGamesByGuids(domainName, guids);
    }

}
