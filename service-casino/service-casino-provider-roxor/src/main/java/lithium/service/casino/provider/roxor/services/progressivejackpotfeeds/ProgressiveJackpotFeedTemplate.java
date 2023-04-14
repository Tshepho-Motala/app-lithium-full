package lithium.service.casino.provider.roxor.services.progressivejackpotfeeds;

import lithium.service.casino.client.objects.Game;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotFeedResponse;
import lithium.service.casino.exceptions.Status510GeneralCasinoExecutionException;
import lithium.service.casino.provider.jackpots.progressive.rest.ProgressiveJackpotFeedRestService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public abstract class ProgressiveJackpotFeedTemplate {
    @Autowired
    ProgressiveJackpotFeedRestService restService;

    public abstract ProgressiveJackpotFeedResponse getProgressiveJackpotFeed(String domainName, List<Game> game) throws Status510GeneralCasinoExecutionException;

    public abstract String getGameSupplier();
}
