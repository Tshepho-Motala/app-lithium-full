package lithium.service.casino.provider.iforium.service.progressivejackpotfeeds;

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
        this.progressiveJackpotFeedTemplateMap = progressiveJackpotFeedTemplates.stream()
                .collect(Collectors.toMap(ProgressiveJackpotFeedTemplate::getGameSupplier, Function.identity()));
    }

    public ProgressiveJackpotFeedResponse getProgressiveJackpotFeed(String gameSupplier, String domainName,
            List<Game> lithiumGames) {
        return getProgressiveJackpotFeedTemplateBySupplier(gameSupplier)
                .get()
                .getProgressiveJackpotFeed(domainName, lithiumGames);
    }

    private Optional<ProgressiveJackpotFeedTemplate> getProgressiveJackpotFeedTemplateBySupplier(String gameSupplier) {
        return ofNullable(progressiveJackpotFeedTemplateMap.get(gameSupplier));
    }
}
