package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.stereotype.Controller;

import lithium.service.casino.provider.supera.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.supera.data.request.GetDemoRequest;
import lithium.service.casino.provider.supera.data.response.GetGameResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class GetDemoController extends BaseController {
	public static final String ACTION_GET_DEMO = "?action=get_demo";
	
	public GetGameResponse getGame(
		String gameId,
		BrandsConfigurationBrand bc
	) throws UnsupportedEncodingException, URISyntaxException {
		GetDemoRequest request = new GetDemoRequest();
		request.setAction(ACTION_GET_DEMO);
		request.setGameId(gameId);
		
		URI uri = new URI(
			bc.getBaseUrl() + 
			bc.getAdminUrl() + request.getAction() + "&" +
			superaService.setRequestParams(bc.getAuthParamMap(), 1) + "&" +
			superaService.setRequestParams(request.parameters(), 0)
		);
		
		GetGameResponse response = null;
		try {
			response = rest.getForObject(uri, GetGameResponse.class);
		} catch (Exception exception) {
			log.error("An error occured while trying to retrieve the demo game | "
					+ "request: " + request.toString() + " | URL: " + uri.toString(), exception);
		}
		
		return response;
	}
}