package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.supera.config.APIAuthentication;
import lithium.service.casino.provider.supera.data.response.AvailableGamesResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AvailableGamesController extends BaseController {
	public static final String ACTION_AVAILABLE_GAMES = "?action=available_games&";
	
	@RequestMapping(value="/games", produces = "application/json")
	public AvailableGamesResponse availableGames(
		APIAuthentication authentication
	) throws URISyntaxException, UnsupportedEncodingException {
		URI uri = new URI(
			authentication.getBrandConfiguration().getBaseUrl() + 
			authentication.getBrandConfiguration().getAdminUrl() +
			ACTION_AVAILABLE_GAMES +
			superaService.setRequestParams(authentication.getBrandConfiguration().getAuthParamMap(), 1)
		);
		
		AvailableGamesResponse response = null;
		try {
			response = rest.getForObject(uri, AvailableGamesResponse.class);
		} catch (Exception exception) {
			log.error("An error occured while trying to retrieve the game list | "
					+ "URL: " + uri.toString(), exception);
		}
		
		return response;
	}
}