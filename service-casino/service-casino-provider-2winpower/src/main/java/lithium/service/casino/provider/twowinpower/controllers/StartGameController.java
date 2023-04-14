package lithium.service.casino.provider.twowinpower.controllers;

import java.util.Map;

import lithium.service.casino.client.Mockable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.provider.twowinpower.TwoWinPowerModuleInfo;
import lithium.service.casino.provider.twowinpower.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.twowinpower.data.GameLocation;
import lithium.service.casino.provider.twowinpower.service.TwoWinPowerService;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.Label;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/games/{domainName}")
public class StartGameController extends Mockable {
	@Autowired
	@Qualifier("lithium.service.casino.provider.twowinpower.resttemplate")
	private RestTemplate restTemplate;
	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private TwoWinPowerModuleInfo moduleInfo;
	@Autowired
	private TwoWinPowerService twpService;
	
	@RequestMapping("/startGame")
	public Response<String> startGame(
		@PathVariable("domainName") String domainName,
		@RequestParam("token") String token,
		@RequestParam("gameId") String gameId,
		@RequestParam("lang") String lang,
		@RequestParam("currency") String currency,
		@RequestParam("os") String os
	) throws Exception {
		log.trace("Start Game");
		log.debug("Token: "+token);
		BrandsConfigurationBrand bc = twpService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
		
		Game game = twpService.getGame(domainName, moduleInfo.getModuleName()+"_"+gameId);
		log.debug("Start Game : "+game);
		
		if (hasLobby(game)) {
			log.warn("Lobby games needs to be implemented.");
			return null;
		}
		
		LithiumTokenUtil tokenUtil = getLithiumTokenFromAuthToken(token);
		
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("game_uuid", gameId);
		parameters.add("player_id", tokenUtil.guid());
		parameters.add("player_name", tokenUtil.username());
		parameters.add("currency", currency);
		parameters.add("session_id", tokenUtil.getJwtUser().getApiToken());
//		parameters.add("return_url", );
		parameters.add("language", lang);
//		parameters.add("email", );
//		parameters.add("lobby_data", );
		log.debug("Parameters :"+parameters);
		
		parameters = twpService.urlEncodeUTF8Map(parameters);
		
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, twpService.buildHeaders(bc.getMerchantId(), bc.getMerchantKey(), parameters.toSingleValueMap()));
		ResponseEntity<GameLocation> result = restTemplate.exchange(
			bc.getBaseUrl()+"/games/init?game_uuid={game_uuid}&player_id={player_id}&player_name={player_name}&currency={currency}&session_id={session_id}&language={language}",
			HttpMethod.POST,
			entity,
			GameLocation.class,
			parameters
		);
		log.debug("Sending : "+result.getBody().getUrl());
		return Response.<String>builder().data(result.getBody().getUrl()).status(Status.OK).build();
	}
	
	@RequestMapping("/demoGame")
	public Response<String> demoGame(
		@PathVariable("domainName") String domainName,
		@RequestParam("gameId") String gameId,
		@RequestParam("lang") String lang,
		@RequestParam("os") String os
	) throws Exception {
		log.debug("Start Demo Game");
		BrandsConfigurationBrand bc = twpService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
		
		Game game = twpService.getGame(domainName, moduleInfo.getModuleName()+"_"+gameId);
		
		if (hasLobby(game)) {
			log.warn("Lobby games needs to be implemented.");
			return null;
		}
		
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("game_uuid", gameId);
		
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, twpService.buildHeaders(bc.getMerchantId(), bc.getMerchantKey(), parameters.toSingleValueMap()));
		ResponseEntity<GameLocation> result = restTemplate.exchange(
			bc.getBaseUrl()+"/games/init-demo?game_uuid={game_uuid}",
			HttpMethod.POST,
			entity,
			GameLocation.class,
			parameters
		);
		
		return Response.<String>builder().data(result.getBody().getUrl()).status(Status.OK).build();
	}
	
	private boolean hasLobby(Game game) {
		Map<String, Label> labels = game.getLabels();
		Label label = labels.get("lobby");
		if (label == null) return false;
		return (Boolean.parseBoolean(label.getValue()));
	}
	
	private LithiumTokenUtil getLithiumTokenFromAuthToken(String authToken) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
		return util;
	}
}
