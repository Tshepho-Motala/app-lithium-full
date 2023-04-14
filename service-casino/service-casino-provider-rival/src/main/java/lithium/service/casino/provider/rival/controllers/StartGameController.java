package lithium.service.casino.provider.rival.controllers;

import lithium.service.casino.client.Mockable;
import lithium.service.casino.provider.rival.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.rival.service.RivalService;
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
import lithium.service.casino.provider.rival.RivalModuleInfo;
import lithium.service.casino.provider.rival.RivalModuleInfo.ConfigProperties;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/games/{domainName}")
public class StartGameController extends Mockable {
	private static final Log log = LogFactory.getLog(StartGameController.class);
	
	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	protected RivalModuleInfo info;

	@Autowired
	RivalService rivalService;

	@RequestMapping("/startGame")
	public Response<String> startGame(@PathVariable("domainName") String domainName, 
			@RequestParam("token") String token, @RequestParam("gameId") String gameId, 
			@RequestParam("lang") String lang, @RequestParam("currency") String currency,
			@RequestParam("os") String os) throws Exception {


		BrandsConfigurationBrand bc = rivalService.getBrandConfiguration(info.getModuleName(), domainName);
		//www.casinocontroller.com/<casinoname>/engine/EmbedGame/EmbedGame.php?game_id=42&playerid=4&sessionid=abc123"></
		String url = bc.getBaseUrl() + "/"+ domainName;
		
		if(os == null || os.equalsIgnoreCase("desktop")) {
			url += "/engine/EmbedGame/EmbedGame.php?";
		} else {
			url += "/html5/dist/?";
		}
		
		url += "game_id=" + gameId + 
				"&playerid="+getuserGuidFromAuthToken(token)+
				"&sessionid="+getApiTokenFromAuthToken(token)+
				"&lang=" + lang+
				"&resize=1";

		url = mockGameStartIfParameterIsSet(bc.isMockActive(), url, token, gameId, currency);

		log.info("start game request gameId " + gameId + " redirecting to url " + url);

		return Response.<String>builder().data(url).status(Status.OK).build();
	}

	//https://demo.casinocontroller.com/beta/luckybetz/engine/EmbedGame/EmbedGame.php?game_id=42&anon=1" 
	@RequestMapping("/demoGame")
	public Response<String> demoGame(@PathVariable("domainName") String domainName, 
			@RequestParam("gameId") String gameId, @RequestParam("lang") String lang,
			@RequestParam("os") String os) throws Exception {
		
		ProviderClient pc = services.target(ProviderClient.class,"service-domain", true);
		String baseUrl = "";
		for(ProviderProperty p : pc.propertiesByProviderUrlAndDomainName(info.getModuleName(), domainName).getData()) {
			if(p.getName().equalsIgnoreCase(ConfigProperties.BASE_URL.getValue())) {
				baseUrl = p.getValue();
			}
		}

//String url = "<iframe src=\"https://demo.casinocontroller.com/beta/luckybetz/engine/EmbedGame/EmbedGame.php?game_id=42&anon=1\" width=\"800\" height=\"600\"></iframe>";

		String url = baseUrl + "/"+ domainName;
		
		if(os == null || os.equalsIgnoreCase("desktop")) {
			url += "/engine/EmbedGame/EmbedGame.php?";
		} else {
			url += "/html5/dist/?";
		}
		
		url += "game_id=" + gameId + "&anon=1&anonOnly=1"+"&lang=" + lang + "&resize=1";

		log.info("start demo request gameId " + gameId + " redirecting to url " + url);

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
