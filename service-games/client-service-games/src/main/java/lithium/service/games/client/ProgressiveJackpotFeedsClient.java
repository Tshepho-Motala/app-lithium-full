package lithium.service.games.client;

import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-games")
public interface ProgressiveJackpotFeedsClient {
    @RequestMapping("/system/jackpot-feeds/progressive/game-balances")
    public List<ProgressiveJackpotGameBalance> getProgressiveGameBalances(@RequestParam("domainName") String domainName);
}

