package lithium.service.casino.client;

import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.exceptions.Status475NullVariablesException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name="service-casino")
public interface SystemBetClient {
    @RequestMapping(value = "/system/bet/round/complete", method = RequestMethod.POST, params = { "domainName", "providerGuid", "roundId"})
    public void completeBetRound(@RequestParam("domainName") String domainName,
                                 @RequestParam("providerGuid") String providerGuid, @RequestParam("roundId") String roundId)
            throws Status474BetRoundNotFoundException;
    @RequestMapping(value = "/system/bet/round/complete", method = RequestMethod.POST, params = { "domainName", "providerGuid", "roundId", "gameGuid", "userGuid" })
    public void completeBetRound(@RequestParam("domainName") String domainName,
                                 @RequestParam("providerGuid") String providerGuid, @RequestParam("roundId") String roundId,  @RequestParam("gameGuid") Optional<String>gameGuid, @RequestParam("userGuid") Optional<String>userGuid)
            throws Status475NullVariablesException;
}
