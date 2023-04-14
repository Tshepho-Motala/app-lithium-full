package lithium.service.casino.client;

import lithium.service.casino.client.objects.response.LastBetResultResponse;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-casino")
public interface BetResultClient {

    @RequestMapping("/system/casino/{domainName}/bet-result/last-bet-result")
    public LastBetResultResponse findLastBetResult(@PathVariable("domainName") String domainName,
                                                   @RequestParam("providerGuid") String providerGuid,
                                                   @RequestParam("roundGuid") String roundGuid) throws Status474BetRoundNotFoundException;
}
