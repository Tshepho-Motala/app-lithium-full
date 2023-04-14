package lithium.service.casino.provider.roxor.services.progressivejackpotfeeds;

import lithium.service.casino.client.objects.Game;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotFeedResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class ProgressiveJackpotFeedService {

    private Map<String, ProgressiveJackpotFeedTemplate> progressiveJackpotFeedTemplateMap;

    public ProgressiveJackpotFeedService(List<ProgressiveJackpotFeedTemplate> progressiveJackpotFeedTemplates) {
        this.progressiveJackpotFeedTemplateMap = progressiveJackpotFeedTemplates.stream().collect(Collectors.toMap(ProgressiveJackpotFeedTemplate::getGameSupplier, Function.identity()));
    }

    public Optional<ProgressiveJackpotFeedTemplate> getProgressiveJackpotFeedTemplateBySupplier(String gameSupplier) {
        return ofNullable(progressiveJackpotFeedTemplateMap.get(gameSupplier));
    }

    public ProgressiveJackpotFeedResponse getJackpotFeed(String domainName, String gameSupplier, List<Game> lithiumGames) {
        ProgressiveJackpotFeedResponse progressiveJackpotFeedResponse = getProgressiveJackpotFeedTemplateBySupplier(gameSupplier).get().getProgressiveJackpotFeed(domainName, lithiumGames);
        return progressiveJackpotFeedResponse;
    }
}
