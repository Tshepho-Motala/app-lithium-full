package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import lithium.service.casino.provider.supera.data.response.GetGameDetails;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.provider.supera.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.supera.data.request.GetGameRequest;
import lithium.service.casino.provider.supera.data.response.GetGameResponse;
import lithium.service.casino.provider.supera.service.SuperaService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class GetGameController {
	
	@Autowired protected RestTemplate rest;
	@Autowired protected SuperaService superaService;
	
	public static final String ACTION_GET_GAME = "?action=get_game";
	
	public GetGameResponse getGame(
		String remoteId,
		String gameId,
		String remoteData,
		BrandsConfigurationBrand bc
	) throws UnsupportedEncodingException, URISyntaxException {

		GetGameRequest request = new GetGameRequest();
		request.setAction(ACTION_GET_GAME);
		request.setRemoteId(remoteId);
		request.setGameId(gameId);
		request.setRemoteData(remoteData);
		
		URI uri = new URI(
			bc.getBaseUrl() + 
			bc.getAdminUrl() + request.getAction() + "&" +
			superaService.setRequestParams(bc.getAuthParamMap(), 1) + "&" +
			superaService.setRequestParams(request.parameters(), 0)
		);
		
		GetGameResponse response = null;
		try {
			if (!bc.isMockActive()) {
				response = rest.getForObject(uri, GetGameResponse.class);
			} else {
				response = new GetGameResponse();
				response.setStatus(HttpStatus.SC_OK);
				response.setResponse(new GetGameDetails());
				response.getResponse().setGameUrl(uri.toString());
			}
		} catch (Exception exception) {
			log.error("An error occured while trying to retrieve the game | "
					+ "request: " + request.toString() + " | URL: " + uri.toString(), exception);
		}
		
		return response;
	}
}