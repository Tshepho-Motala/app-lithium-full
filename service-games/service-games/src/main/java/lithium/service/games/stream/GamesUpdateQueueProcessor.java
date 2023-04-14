package lithium.service.games.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.games.client.objects.GameStream;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.objects.GameGraphicBasic;
import lithium.service.games.services.GameService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableBinding(GamesUpdateQueueSink.class)
public class GamesUpdateQueueProcessor {
	@Autowired
	private GameService gameService;
	
	@StreamListener(GamesUpdateQueueSink.INPUT)
	void handle(GameStream gameStream) throws Exception {
		log.info("Game received for registration: " + gameStream.getGame());
		//Always add provider games with default domain
		Game game = gameService.addGame(gameStream.getGame(), "default", true, true);
		log.info("Saved : "+game);
		if (gameStream.getGraphic()!=null) {
			GameGraphicBasic ggb = GameGraphicBasic.builder()
			.deleted(false)
			.domainName("default")
			.enabled(true)
			.gameId(game.getId())
			.graphicFunctionName(gameStream.getGraphic().getGraphicFunctionName())
			.image(gameStream.getGraphic().getImage())
			.build();
			log.info("Saving Graphic : "+gameStream.getGraphic());
			gameService.saveGameGraphic(ggb);
		} else {
			log.warn("No image to save for : "+game);
		}
	}
}
