package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.provider.supera.SuperaModuleInfo;
import lithium.service.casino.provider.supera.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.supera.data.request.FreeroundShowRequest;
import lithium.service.casino.provider.supera.data.response.FreeroundShowResponse;
import lithium.service.casino.provider.supera.service.SuperaService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FreeroundShowController {
	
	@Autowired protected SuperaService superaService;
	
	@Autowired protected SuperaModuleInfo moduleInfo;
	
	@Autowired protected RestTemplate rest;
	
	private static final String ACTION_FREEROUND_SHOW = "?action=show";
	
	public FreeroundShowResponse freeroundShow(
		String gameId,
		String remoteId,
		String domainName
	) throws UnsupportedEncodingException, URISyntaxException {
		
		BrandsConfigurationBrand bc = superaService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
		
		FreeroundShowRequest request = new FreeroundShowRequest(gameId, remoteId);
		request.setAction(ACTION_FREEROUND_SHOW);
		
		URI uri = new URI(
			bc.getBaseUrl() + 
			bc.getFreeroundsUrl() + request.getAction() + "&" +
			superaService.setRequestParams(bc.getAuthParamMap(), 1) + "&" +
			superaService.setRequestParams(request.parameters(), 0)
		);
		
		FreeroundShowResponse response = null;
		try {
			response = rest.getForObject(uri, FreeroundShowResponse.class);
		} catch (Exception exception) {
			log.error("An error occured while trying to access freeround show service | "
					+ "request: " + request.toString() + " | URL: " + uri.toString(), exception);
		}
		
		return response;
	}
}