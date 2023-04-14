package lithium.service.casino.mock.twowinpower.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.provider.twowinpower.data.GameLocation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/games")
public class StartGameController {
	@Autowired
	@Qualifier("lithium.service.casino.mock.twowinpower.resttemplate")
	private RestTemplate restTemplate;
	
	@RequestMapping("/init")
	public GameLocation startGame(
		@RequestParam("game_uuid") String gameUuid,
		@RequestParam("player_id") String playerId,
		@RequestParam("player_name") String playerName,
		@RequestParam("currency") String currency,
		@RequestParam("session_id") String sessionId,
		@RequestParam(value="return_url", required=false) String returnUrl,
		@RequestParam(value="language", required=false) String language,
		@RequestParam(value="email", required=false) String email,
		@RequestParam(value="lobby_data", required=false) String lobbyData
	) throws Exception {
		log.info("Start Game");
		return GameLocation.builder().url("http://www.google.co.za").build();
	}
	
	@RequestMapping("/init-demo")
	public GameLocation demoGame(
		@RequestParam(value="game_uuid", required=true) String gameId,
		@RequestParam(value="lang", required=false) String lang,
		@RequestParam(value="return_url", required=false) String returnUrl
	) throws Exception {
		log.info("Start Demo Game");
		
		return GameLocation.builder().url("http://www.google.co.za").build();
	}
}
