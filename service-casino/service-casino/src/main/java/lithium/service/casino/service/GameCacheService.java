package lithium.service.casino.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.Game;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GameCacheService {

	@Autowired
	private LithiumServiceClientFactory services;
	


	@Cacheable("lithium.service.casino.services.gamename")
	public String findGameNameByGuidAndDomain(final String gameGuid, final String domainName) {
		Response<Game> game = null;
		String gameName = "Freespin Bonus";
		try {
			game = getGamesClient().findByGuidAndDomainName(domainName, gameGuid);
			if (game.getStatus() == Status.OK) {
				gameName = game.getData().getName();
			}
		} catch (Exception e) {
			log.warn("Could not find game guid: " + gameGuid + " on domain: " + domainName, e);
		}
		return gameName;
	}
	
	private GamesClient getGamesClient() {
		GamesClient gc = null;
		try {
			gc = services.target(GamesClient.class, "service-games", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting games service", e);
		}
		return gc;
	}
}
