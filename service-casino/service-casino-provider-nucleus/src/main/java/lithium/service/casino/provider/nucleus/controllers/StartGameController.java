package lithium.service.casino.provider.nucleus.controllers;

import lithium.service.casino.client.Mockable;
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
import lithium.service.casino.provider.nucleus.NucleusModuleInfo;
import lithium.service.casino.provider.nucleus.NucleusModuleInfo.ConfigProperties;
import lithium.service.casino.provider.nucleus.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.nucleus.service.NucleusService;
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
	protected NucleusModuleInfo moduleInfo; 
	
	@Autowired
	protected NucleusService nucleusService;

	@RequestMapping("/startGame")
	public Response<String> startGame(@PathVariable("domainName") String domainName,
	                                  @RequestParam("token") String token, @RequestParam("gameId") String gameId,
	                                  @RequestParam("lang") String lang, @RequestParam("currency") String currency,
	                                  @RequestParam("os") String os) throws Exception {

		BrandsConfigurationBrand bc = nucleusService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);

		String url = bc.getBaseUrl();
		url += "/cwstartgamev2.do?mode=real&token=" + getApiTokenFromAuthToken(token) + "&";
		url += "bankId=" + bc.getBankId() + "&gameId=" + gameId + "&lang=" + lang;

		log.info("start game request gameId " + gameId + " redirecting to url " + url);

		url = mockGameStartIfParameterIsSet(bc.isMockActive(), url, token, gameId, currency);

		return Response.<String>builder().data(url).status(Status.OK).build();
	}

	@RequestMapping("/demoGame")
	public Response<String> demoGame(@PathVariable("domainName") String domainName,
	                                 @RequestParam("gameId") String gameId, @RequestParam("lang") String lang,
	                                 @RequestParam("os") String os) throws Exception {

		ProviderClient pc = services.target(ProviderClient.class,"service-domain", true);
		String bankId = "";
		String baseUrl = "";
		for(ProviderProperty p : pc.propertiesByProviderUrlAndDomainName(moduleInfo.getModuleName(), domainName).getData()) {
			if(p.getName().equalsIgnoreCase(ConfigProperties.BANK_ID.getValue())) {
				bankId = p.getValue();
			}
			if(p.getName().equalsIgnoreCase(ConfigProperties.BASE_URL.getValue())) {
				baseUrl = p.getValue();
			}
		}

		String url = baseUrl;
		url += "/cwguestlogin.do?";
		url += "bankId=" + bankId + "&gameId=" + gameId + "&lang=" + lang;

		log.info("start game request gameId " + gameId + " redirecting to url " + url);

		return Response.<String>builder().data(url).status(Status.OK).build();
	}

	private String getApiTokenFromAuthToken(String authToken) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
		String apiToken = util.getJwtUser().getApiToken();
		apiToken += "|"+util.getJwtUser().getDomainName() +"/"+util.getJwtUser().getUsername();
		return apiToken;
	}
}
