package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.supera.config.APIAuthentication;
import lithium.service.casino.provider.supera.data.request.GameRoundRequest;
import lithium.service.casino.provider.supera.data.response.GameRoundResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class GameRoundController extends BaseController {
	private static final String ACTION_GAME_ROUND = "?action=game_round";
	
	@RequestMapping(value = "/gameround", produces = "application/json")
	public GameRoundResponse gameRound(
		@RequestParam Integer gameId,
		@RequestParam Integer gameRoundId,
		APIAuthentication authentication
	) throws UnsupportedEncodingException, URISyntaxException {
		GameRoundRequest request = new GameRoundRequest(gameId, gameRoundId);
		request.setAction(ACTION_GAME_ROUND);
		
		URI uri = new URI(
			authentication.getBrandConfiguration().getBaseUrl() + 
			authentication.getBrandConfiguration().getAdminUrl() + request.getAction() + "&" +
			superaService.setRequestParams(authentication.getBrandConfiguration().getAuthParamMap(), 1) + "&" +
			superaService.setRequestParams(request.parameters(), 0)
		);
		
		log.info("uri :: " + uri.toString());
		
		GameRoundResponse response = null;
		try {
			response = rest.getForObject(uri, GameRoundResponse.class);
		} catch (Exception exception) {
			log.error("An error occured while trying to retrieve the game round | "
					+ "request: " + request.toString() + " | URL: " + uri.toString(), exception);
		}
		
		return response;
	}
}