package lithium.service.games.controllers.system;

import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance;
import lithium.service.games.client.ProgressiveJackpotFeedsClient;
import lithium.service.games.services.ProgressiveJackpotFeedsBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class SystemProgressiveGameBalanceController implements ProgressiveJackpotFeedsClient {
    @Autowired private ProgressiveJackpotFeedsBalanceService service;

    @Override
    @RequestMapping("/system/jackpot-feeds/progressive/game-balances")
    public List<ProgressiveJackpotGameBalance> getProgressiveGameBalances(@RequestParam("domainName") String domainName) {
        return service.getProgressiveJackpotGameBalanceByDomain(domainName);
    }
}
