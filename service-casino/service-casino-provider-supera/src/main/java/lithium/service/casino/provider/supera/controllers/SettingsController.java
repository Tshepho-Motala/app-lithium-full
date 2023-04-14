package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.supera.config.APIAuthentication;
import lithium.service.casino.provider.supera.data.request.SettingsRequest;
import lithium.service.casino.provider.supera.data.response.CasinoSettingsResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SettingsController extends BaseController {
	private static final String ACTION_SETTINGS = "?action=settings";
	
	// if no args are passed, the current casino settings are displayed
	// ^ does not look like this is the case, CasinoSettingsResponse is sent, regardless !!
	@RequestMapping(value = "/settings", produces = "application/json")
	public Object settings(
		@RequestParam(required = false) Integer sessionExpire,
		@RequestParam(required = false) Integer sessionStartExpire,
		@RequestParam(required = false) String media,
		@RequestParam(required = false) String service,
		APIAuthentication authentication
	) throws UnsupportedEncodingException, URISyntaxException {
		SettingsRequest request = new SettingsRequest(sessionExpire, sessionStartExpire, media, service);
		request.setAction(ACTION_SETTINGS);
		
		URI uri = new URI(
			authentication.getBrandConfiguration().getBaseUrl() + 
			authentication.getBrandConfiguration().getAdminUrl() + request.getAction() + "&" +
			superaService.setRequestParams(authentication.getBrandConfiguration().getAuthParamMap(), 1) + "&" +
			superaService.setRequestParams(request.parameters(), 0)
		);
		
		log.info("uri :: " + uri.toString());
		
//		if (request.getSessionExpire() == null && request.getSessionStartExpire() == null && media == null && service == null) {
			CasinoSettingsResponse response = null;
			try {
				response = rest.getForObject(uri, CasinoSettingsResponse.class);
			} catch (Exception exception) {
				log.error("An error occured while trying to access the settings service | "
						+ "request: " + request.toString() + " | URL: " + uri.toString(), exception);
			}
			return response;
//		}
		
/*
		SettingsResponse response = null;

		try {
			response = rest.getForObject(uri, SettingsResponse.class);
		} catch (Exception exception) {
			log.error("An error occured while trying to access the settings service | "
					+ "request: " + request.toString() + " | URL: " + uri.toString(), exception);
		}
		return response;
*/
	}
}