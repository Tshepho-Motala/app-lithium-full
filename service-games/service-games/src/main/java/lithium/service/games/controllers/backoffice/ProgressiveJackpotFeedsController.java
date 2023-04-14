package lithium.service.games.controllers.backoffice;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotFeed;
import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotGameBalance;
import lithium.service.games.services.ProgressiveJackpotFeedsBalanceService;
import lithium.service.games.services.ProgressiveJackpotFeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/backoffice/jackpot-feeds/progressive")
public class ProgressiveJackpotFeedsController {
    @Autowired
    ProgressiveJackpotFeedService progressiveJackpotFeedService;

    @Autowired
    ProgressiveJackpotFeedsBalanceService progressiveJackpotFeedsBalanceService;

    @PostMapping("/{domainName}/registered-feeds/table")
    public DataTableResponse<ProgressiveJackpotFeed> getProgressiveRegisteredFeeds(
            @PathVariable("domainName") String domainName,
            DataTableRequest request
    ) {
        Page<ProgressiveJackpotFeed> progressiveJackpotFeedRegistrations = progressiveJackpotFeedService
                .getAllRegisteredProgressiveFeedsByDomain(domainName, request.getPageRequest());
        return new DataTableResponse<>(request, progressiveJackpotFeedRegistrations);
    }

    @PostMapping("/{domainName}/table")
    public DataTableResponse<ProgressiveJackpotGameBalance> getProgressiveBalances(
            @PathVariable("domainName") String domainName,
            DataTableRequest request
    ) {
        Page<ProgressiveJackpotGameBalance> progressiveJackpotGameBalances = progressiveJackpotFeedsBalanceService.findByDomain(domainName, request.getPageRequest());
        return new DataTableResponse<>(request, progressiveJackpotGameBalances);
    }

    @PostMapping("/{domainName}/progressive-jackpot-game-balance/get")
    public List<lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance> getProgressiveGameBalancesByDomain(
            @PathVariable("domainName") String domainName
    ) {
        return progressiveJackpotFeedsBalanceService.getProgressiveJackpotGameBalanceByDomain(domainName);
    }

    @PostMapping("/{domainName}/progressive-jackpot-balance/get")
    public List<lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotBalance> getProgressiveJackpotBalanceByDomain(
            @PathVariable("domainName") String domainName
    ) {
        return progressiveJackpotFeedsBalanceService.getProgressiveJackpotFeedsByDomain(domainName);
    }
}
