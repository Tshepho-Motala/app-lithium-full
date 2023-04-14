package lithium.service.games;

import lithium.service.games.client.objects.Domain;
import lithium.service.games.client.objects.Game;
import lithium.service.games.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import lithium.service.Response;
import lithium.service.games.controllers.DomainGamesController;
import lithium.service.games.controllers.GamesController;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ServiceGamesInit {
	@Autowired
	GamesController gamesController;
	@Autowired
	GameService gameService;
	@Autowired
	DomainGamesController domainGamesController;

	@Retryable(backoff=@Backoff(5000), maxAttempts=100)
	@Async
	public void init() throws Exception {
		gamesController.updateProviderGames();
		Response<Iterable<Game>> gameList = domainGamesController.listDomainGames("default");
		for(Game g: gameList.getData()) {
			g.setEnabled(true);
			g.setVisible(true);
			gamesController.edit(g, null, null);
			
			g.setId(0);
			g.setDomain(Domain.builder().name("luckybetz").build());
			gameService.editGame(g, true, true, null);
			
			g.setId(0);
			g.setDomain(Domain.builder().name("toft").build());
			gameService.editGame(g, true, true, null);
		}
		log.info("FAKE DATA WRITTEN TO ENVIRONMENT");
	}
}
