package lithium.service.games.controllers.system;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.games.client.RecommendedGamesClient;
import lithium.service.games.client.objects.RecentlyPlayedGameBasic;
import lithium.service.games.client.objects.RecommendedGameBasic;
import lithium.service.games.services.RecentlyPlayedService;
import lithium.service.games.services.RecommendedGamesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/recommended-games")
@Slf4j
public class SystemRecommendedGamesController implements RecommendedGamesClient {

	@Autowired private RecommendedGamesService service;

	@GetMapping
	public List<RecommendedGameBasic> getRecommendedGamesBasics(@RequestParam("userGuid") String userGuid,
														  @RequestParam(name = "liveCasino", required = false) Boolean liveCasino,
														  @RequestParam(name = "channel", required = false) String channel,
														  @RequestParam(name = "locale", defaultValue = "en_US") String locale)
			throws LithiumServiceClientFactoryException, Status550ServiceDomainClientException,
			Status474DomainProviderDisabledException, Status500InternalServerErrorException {
		return service.getRecommendedGames(userGuid, liveCasino, channel, locale);
	}

}
