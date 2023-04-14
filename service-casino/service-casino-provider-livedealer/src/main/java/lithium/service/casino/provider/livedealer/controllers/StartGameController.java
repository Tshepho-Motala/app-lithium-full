package lithium.service.casino.provider.livedealer.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.provider.livedealer.LivedealerModuleInfo;
import lithium.service.casino.provider.livedealer.LivedealerModuleInfo.ConfigProperties;
import lithium.service.casino.provider.livedealer.service.LivedealerService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/games/{domainName}")
public class StartGameController {
	private static final Log log = LogFactory.getLog(StartGameController.class);
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	protected LivedealerModuleInfo info;
	
	@Autowired
	protected LivedealerService livedealerService;

	@RequestMapping("/startGame")
	public Response<String> startGame(@PathVariable("domainName") String domainName, 
			@RequestParam("token") String token, @RequestParam("gameId") String gameId, 
			@RequestParam("lang") String lang, @RequestParam("currency") String currency,
			@RequestParam("os") String os) throws Exception {
			
		
		
		;
		String url = livedealerService.getBrandConfiguration(info.getModuleName(), domainName).getBaseUrl() 
		+ "/livedealer/liveDealerLobby.aspx?"
		+ "data="+ livedealerService.getLiveDealerToken(info.getModuleName(), domainName, getuserGuidFromAuthToken(token));

		log.info("start game request gameId " + gameId + " redirecting to url " + url);

		return Response.<String>builder().data(url).status(Status.OK).build();
	}

	@RequestMapping("/games/{domainName}/demoGame")
	public Response<String> demoGame(@PathVariable("domainName") String domainName, 
			@RequestParam("gameId") String gameId, @RequestParam("lang") String lang) throws Exception {
		
		String url = livedealerService.getBrandConfiguration(info.getModuleName(), domainName).getBaseUrl() 
		+ "/livedealer/liveDealerLobby.aspx?"
		+ "data="+ livedealerService.getLiveDealerToken(info.getModuleName(), domainName, "demouser");
		
		return Response.<String>builder().data(url).status(Status.OK).build();
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
