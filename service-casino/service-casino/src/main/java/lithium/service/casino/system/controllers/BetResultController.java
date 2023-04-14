package lithium.service.casino.system.controllers;

import lithium.service.casino.client.objects.response.LastBetResultResponse;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.service.BetResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system/casino/{domainName}/bet-result")
public class BetResultController {

    @Autowired
    private BetResultService betResultService;

    @GetMapping("last-bet-result")
    public LastBetResultResponse findLastBetResult(@PathVariable("domainName") String domainName,
                                                   @RequestParam("providerGuid") String providerGuid,
                                                   @RequestParam("roundGuid") String roundGuid)
            throws Status474BetRoundNotFoundException {
        log.debug("Last bet result request: domainName: " + domainName + " providerGuid: " + providerGuid + " roundGuid: " + roundGuid);
        LastBetResultResponse lastBetResultResponse = betResultService.retrieveLastBetResultByRoundGuid(domainName, providerGuid, roundGuid);
        log.debug("Last bet result response: lastBetResult: " + lastBetResultResponse);
        return lastBetResultResponse;
    }
}
