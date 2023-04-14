package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.provider.supera.SuperaModuleInfo;
import lithium.service.casino.provider.supera.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.supera.data.request.FreeroundRemoveRequest;
import lithium.service.casino.provider.supera.data.response.FreeroundRemoveResponse;
import lithium.service.casino.provider.supera.service.SuperaService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FreeroundRemoveController extends BaseController {
	@Autowired protected SuperaService superaService;
	
	@Autowired protected SuperaModuleInfo moduleInfo;
	
	@Autowired protected RestTemplate rest;
	
	private static final String ACTION_FREEROUND_REMOVE = "?action=remove";
	
	//Freeround setup id is the one returned when freerounds are allocated to a player
	public FreeroundRemoveResponse freeroundRemove(
		String gameId,
		String remoteId,
		Integer freeroundSetupId,
		String domainName
	) throws UnsupportedEncodingException, URISyntaxException {
		
		BrandsConfigurationBrand bc = superaService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
		
		FreeroundRemoveRequest request = new FreeroundRemoveRequest(gameId, remoteId, freeroundSetupId);
		request.setAction(ACTION_FREEROUND_REMOVE);
		
		URI uri = new URI(
			bc.getBaseUrl() + 
			bc.getFreeroundsUrl() + request.getAction() + "&" +
			superaService.setRequestParams(bc.getAuthParamMap(), 1)
		);
		
		FreeroundRemoveResponse response = null;
		try {
			response = rest.getForObject(uri, FreeroundRemoveResponse.class);
		} catch (Exception exception) {
			log.error("An error occured while trying to access freeround remove service | "
					+ "request: " + request.toString() + " | URL: " + uri.toString(), exception);
		}
		
		return response;
	}
}