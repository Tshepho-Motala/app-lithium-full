package lithium.service.casino.provider.supera.controllers;

import lithium.service.casino.client.Mockable;
import lithium.service.casino.provider.supera.config.BrandsConfigurationBrand;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.provider.supera.SuperaModuleInfo;
import lithium.service.casino.provider.supera.data.response.GetGameResponse;
import lithium.service.casino.provider.supera.service.SuperaService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/games/{domainName}")
public class StartGameController extends Mockable {
	
	@Autowired protected SuperaModuleInfo info;
	
	@Autowired protected TokenStore tokenStore;
	
	@Autowired protected SuperaService superaService;
	
	@Autowired protected GetGameController getGameController;
	
	@Autowired protected GetDemoController getDemoController;
	
	@RequestMapping("/startGame")
	public Response<String> startGame(@PathVariable("domainName") String domainName, 
			@RequestParam("token") String token, @RequestParam("gameId") String gameId, 
			@RequestParam("lang") String lang, @RequestParam("currency") String currency,
			@RequestParam("os") String os) throws Exception {

		BrandsConfigurationBrand bc = superaService.getBrandConfiguration(info.getModuleName(), domainName);
		GetGameResponse ggr = getGameController.getGame(getuserGuidFromAuthToken(token),
				gameId, getApiTokenFromAuthToken(token),
				bc);
		
		if(ggr.getStatus() != HttpStatus.SC_OK) {
			log.error("Error getting game URL for: " + gameId + " response: "+ggr);
			return Response.<String>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}

		String url = ggr.getResponse().getGameUrl();

		url = mockGameStartIfParameterIsSet(bc.isMockActive(), url, token, gameId, currency);

		return Response.<String>builder().data(url).status(Status.OK).build();
	}
	
	@RequestMapping("/demoGame")
	public Response<String> demoGame(@PathVariable("domainName") String domainName, 
			@RequestParam("gameId") String gameId, @RequestParam("lang") String lang,
			@RequestParam("os") String os) throws Exception {
		GetGameResponse ggr = getDemoController.getGame(gameId, 
				superaService.getBrandConfiguration(info.getModuleName(), domainName));
		
		if(ggr.getStatus() != HttpStatus.SC_OK) {
			log.error("Error getting demo game URL for: " + gameId + " response: "+ggr);
			return Response.<String>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.<String>builder().data(ggr.getResponse().getGameUrl()).status(Status.OK).build();
	}
	
	private String getApiTokenFromAuthToken(String authToken) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
		String apiToken = util.getJwtUser().getApiToken();
		//apiToken += "|"+util.getJwtUser().getDomainName() +"/"+util.getJwtUser().getUsername();
		return apiToken;
	}
	
	private String getuserGuidFromAuthToken(String authToken) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
		String userGuid = util.getJwtUser().getDomainName() +"/"+util.getJwtUser().getUsername();
		return userGuid;
	}
}