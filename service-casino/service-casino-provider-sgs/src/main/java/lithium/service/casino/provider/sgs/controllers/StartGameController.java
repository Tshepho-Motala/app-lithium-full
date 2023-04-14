package lithium.service.casino.provider.sgs.controllers;

import lithium.service.casino.client.Mockable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.provider.sgs.SGSModuleInfo;
import lithium.service.casino.provider.sgs.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.sgs.service.SGSService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/games/{domainName}")
public class StartGameController extends Mockable {
	
	@Autowired
	protected SGSModuleInfo moduleInfo; 
	
	@Autowired
	protected SGSService sgsService;

	@RequestMapping("/startGame")
	public Response<String> startGame(@PathVariable("domainName") String domainName, 
			@RequestParam("token") String token, @RequestParam("gameId") String gameId, 
			@RequestParam("lang") String lang) throws Exception {
		
		BrandsConfigurationBrand bc = sgsService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
		
		String url = bc.getBaseUrl();
		url += "/startGame?AuthToken=" + sgsService.getApiTokenFromAuthToken(token) + "&";
		url += "csid=" + bc.getCustomerId() + "&sext2=genauth" + "&gameid=" + gameId + "&lang=" + lang + "&backURL=" + "";

		url = mockGameStartIfParameterIsSet(bc.isMockActive(), url, token, gameId, bc.getCurrency());

		log.info("start game request gameId " + gameId + " redirecting to url " + url);

		return Response.<String>builder().data(url).status(Status.OK).build();
	}

	@RequestMapping("/demoGame")
	public Response<String> demoGame(@PathVariable("domainName") String domainName, 
			@RequestParam("gameId") String gameId, @RequestParam("lang") String lang) throws Exception {
		
		BrandsConfigurationBrand bc = sgsService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
		
		String url = bc.getBaseUrl();
		url += "/startGame?";
		url += "csid=" + bc.getCustomerId() + "&sext2=demo" + "&gameid=" + gameId + "&lang=" + lang + "&backURL=" + "";

		log.info("start game request gameId " + gameId + " redirecting to url " + url);

		return Response.<String>builder().data(url).status(Status.OK).build();
	}
}
