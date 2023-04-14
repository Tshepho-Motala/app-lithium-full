package lithium.service.casino.provider.roxor.api.controllers;

import lithium.service.casino.client.ProgressiveJackpotFeedsClient;
import lithium.service.casino.client.objects.Game;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotFeedResponse;
import lithium.service.casino.exceptions.Status510GeneralCasinoExecutionException;
import lithium.service.casino.provider.roxor.services.progressivejackpotfeeds.ProgressiveJackpotFeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/system/jackpot-feed/progressive")
public class ProgressiveJackpotFeedController implements ProgressiveJackpotFeedsClient {

    @Autowired
    ProgressiveJackpotFeedService progressiveJackpotFeedService;

    @PostMapping("/{domainName}/{gameSupplier}/get")
    @Override
    public ProgressiveJackpotFeedResponse getProgressiveJackpotFeeds(
            @PathVariable("domainName") String domainName,
            @PathVariable("gameSupplier") String gameSupplier,
            @RequestBody List<Game> lithiumGames
    ) throws Status510GeneralCasinoExecutionException {
        return progressiveJackpotFeedService.getJackpotFeed(domainName, gameSupplier, lithiumGames);
    }
}
