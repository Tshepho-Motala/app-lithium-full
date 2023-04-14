package lithium.service.games.controllers.frontend;

import lithium.service.games.data.objects.ProgressiveJackpotBalanceFE;
import lithium.service.games.services.ProgressiveJackpotFeedsBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/frontend/jackpot-feeds/progressive")
public class FrontEndProgressiveJackpotFeedsController {
    @Autowired private ProgressiveJackpotFeedsBalanceService service;

    @PostMapping("/{domainName}/get/v1")
    public List<ProgressiveJackpotBalanceFE> getProgressiveJackpotFeeds(@PathVariable("domainName") String domainName) {
        return service.getProgressiveJackpotFeedsByDomain(domainName)
                .stream()
                .map(balance -> ProgressiveJackpotBalanceFE.builder()
                        .progressiveId(balance.getProgressiveId())
                        .currencyCode(balance.getCurrencyCode())
                        .amount(balance.getAmount())
                        .wonByAmount(balance.getWonByAmount())
                        .build())
                .toList();
    }
}
