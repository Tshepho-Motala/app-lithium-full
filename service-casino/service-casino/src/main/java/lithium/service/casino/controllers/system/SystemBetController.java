package lithium.service.casino.controllers.system;

import java.util.Optional;
import lithium.service.casino.client.SystemBetClient;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.exceptions.Status475NullVariablesException;
import lithium.service.casino.service.BetResultPersistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/bet")
public class SystemBetController implements SystemBetClient {
	@Autowired BetResultPersistService service;

	@PostMapping(value = "/round/complete", params = { "domainName", "providerGuid", "roundId" })
	public void completeBetRound(@RequestParam("domainName") String domainName,
								 @RequestParam("providerGuid")String providerGuid,
								 @RequestParam("roundId")String roundId)
			throws Status474BetRoundNotFoundException {
		service.completeBetRound(domainName, providerGuid, roundId);
	}
	@PostMapping(value = "/round/complete", params = { "domainName", "providerGuid", "roundId", "gameGuid", "userGuid" })
	public void completeBetRound(@RequestParam("domainName") String domainName,
								 @RequestParam("providerGuid") String providerGuid, @RequestParam("roundId") String roundId, @RequestParam("gameGuid") Optional<String> gameGuid, @RequestParam("userGuid") Optional<String> userGuid)
			throws Status475NullVariablesException{
		service.completeBetRound(domainName, providerGuid, roundId, gameGuid, userGuid);
	}

}
