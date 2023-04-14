package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.provider.supera.SuperaModuleInfo;
import lithium.service.casino.provider.supera.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.supera.data.request.FreeroundAddRequest;
import lithium.service.casino.provider.supera.data.response.FreeroundAddResponse;
import lithium.service.casino.provider.supera.service.SuperaService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FreeroundAddController extends BaseController {
	
	@Autowired protected SuperaService superaService;
	
	@Autowired protected SuperaModuleInfo moduleInfo;
	
	@Autowired protected RestTemplate rest;
	
	private static final String ACTION_FREEROUND_ADD = "?action=add";
	
	//The freeround bets service returns the possible ids to use for betId field
	public FreeroundAddResponse freeroundAdd(
		String gameId,
		String remoteId,
		Integer betId,
		Integer count,
		Integer timeToLast,
		DateTime validFrom,
		DateTime validTo,
		String domainName
	) throws UnsupportedEncodingException, URISyntaxException {
		
		BrandsConfigurationBrand bc = superaService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
		
		FreeroundAddRequest request = new FreeroundAddRequest();
		request.setAction(ACTION_FREEROUND_ADD);
		request.setGameId(gameId);
		request.setRemoteId(remoteId);
		request.setBetId(betId);
		request.setCount(count);
		request.setTimeToLast(timeToLast);
		request.setValidFrom(validFrom);
		request.setValidTo(validTo);
		
		URI uri = new URI(
			bc.getBaseUrl() + 
			bc.getFreeroundsUrl() + request.getAction() + "&" +
			superaService.setRequestParams(bc.getAuthParamMap(), 1) + "&" +
			superaService.setRequestParams(request.parameters(), 0)
		);
		
		FreeroundAddResponse response = null;
		try {
			response = rest.getForObject(uri, FreeroundAddResponse.class);
		} catch (Exception exception) {
			log.error("An error occured while trying to access freeround add service | "
					+ "request: " + request.toString() + " | URL: " + uri.toString(), exception);
		}
		
		return response;
	}
}