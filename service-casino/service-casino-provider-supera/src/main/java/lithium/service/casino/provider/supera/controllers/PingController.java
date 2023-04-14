package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.supera.config.APIAuthentication;
import lithium.service.casino.provider.supera.data.response.PingResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PingController extends BaseController {
	private static final String ACTION_PING = "?action=ping&";
	
	@RequestMapping(value="/ping", produces = "application/json")
	public PingResponse ping(
		APIAuthentication authentication
	) throws URISyntaxException, UnsupportedEncodingException {
		URI uri = new URI(
			authentication.getBrandConfiguration().getBaseUrl() + 
			authentication.getBrandConfiguration().getAdminUrl() +
			ACTION_PING +
			superaService.setRequestParams(authentication.getBrandConfiguration().getAuthParamMap(), 1)
		);
		
		PingResponse response = null;
		try {
			response = rest.getForObject(uri, PingResponse.class);
		} catch (Exception exception) {
			log.error("An error occured while trying to send a ping request to the bog server | "
					+ "URL: " + uri.toString(), exception);
		}
		return response;
	}
}