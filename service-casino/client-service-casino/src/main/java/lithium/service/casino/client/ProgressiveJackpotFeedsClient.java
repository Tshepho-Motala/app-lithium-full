package lithium.service.casino.client;

import lithium.service.casino.client.objects.Game;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotFeedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name="service-casino", path="/system/jackpot-feed/progressive")
public interface ProgressiveJackpotFeedsClient {
    @RequestMapping(value = "/{domainName}/{gameSupplier}/get", method = RequestMethod.POST)
    public ProgressiveJackpotFeedResponse getProgressiveJackpotFeeds(@PathVariable("domainName") String domainName,
                                                                     @PathVariable("gameSupplier") String gameSupplier,
                                                                     @RequestBody List<Game> game
    );
}
