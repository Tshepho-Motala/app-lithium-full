package lithium.service.casino;

import lithium.service.casino.client.ProgressiveJackpotFeedsClient;
import lithium.service.casino.client.objects.Game;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotFeedResponse;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProgressiveJackpotFeedsClientService {
    @Autowired private LithiumServiceClientFactory services;

    public ProgressiveJackpotFeedResponse getProgressiveJackpotFeed(String domainName, String module,
            String gameSupplier, List<Game> game) {
        ProgressiveJackpotFeedResponse response = getProgressiveJackPotFeedsClient(module).orElse(null)
                .getProgressiveJackpotFeeds(domainName, gameSupplier, game);
        log.debug("Progressive Jackpot Feed Standard API Response: " + response);
        return response;
    }

    private Optional<ProgressiveJackpotFeedsClient> getProgressiveJackPotFeedsClient(String moduleName) {
        return getClient(ProgressiveJackpotFeedsClient.class, moduleName);
    }

    private <E> Optional<E> getClient(Class<E> theClass, String url) {
        E clientInstance = null;

        try {
            clientInstance = services.target(theClass, url, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage(), e);
        }

        return Optional.ofNullable(clientInstance);
    }
}
