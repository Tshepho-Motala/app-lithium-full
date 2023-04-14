package lithium.service.casino.provider.betsoft.controllers;

import lithium.service.casino.provider.betsoft.BetsoftModuleInfo;
import lithium.service.casino.provider.betsoft.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.betsoft.data.GameInternal;
import lithium.service.casino.provider.betsoft.data.GamesSuitesInternal;
import lithium.service.casino.provider.betsoft.data.SuiteInternal;
import lithium.service.casino.provider.betsoft.service.BetsoftService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.Label;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/games/{domainName}")
public class GameListController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	protected BetsoftModuleInfo moduleInfo;
	
	@Autowired
	protected BetsoftService betsoftService;
	
	private static List<String> osList = new ArrayList<String>();
	static {
		osList.add("Windows Phone");
		osList.add("Mobile");
		osList.add("Android");
		osList.add("Desktop"); //Bottom of list is always default
	}
	
	@RequestMapping(path="/listGames")
	public List<Game> listGames(@PathVariable("domainName") String domainName) throws Exception {
		try {
			BrandsConfigurationBrand bc = betsoftService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
			
			//Test list if betsoft is not available
			//GamesSuitesInternal gameListInternal = restTemplate.getForObject("http://localhost:9800/scripts/controllers/dashboard/games/list/test/bslist.xml", GamesSuitesInternal.class);
			
			GamesSuitesInternal gameListInternal = restTemplate.postForObject(bc.getBaseUrl()+"/gamelist.do?bankId="+bc.getBankId(), null, GamesSuitesInternal.class);
			//GamesSuitesInternal gameListInternal = restTemplate.postForObject("https://lobby-ffp.discreetgaming.com/gamelist.do?bankId=936", null, GamesSuitesInternal.class);

			List<Game> gameList = new ArrayList<Game>();
			for(SuiteInternal suite : gameListInternal.getSuiteList()) {
				for(GameInternal gameInternal : suite.getGameList()) {
					
					HashMap<String, Label> labels = new HashMap<>();
					labels.put("category", Label.builder().name("category").value(suite.getName().toLowerCase()).domainName(domainName).build());
					labels.put("os", Label.builder().name("os").value(getOperatingSystem(gameInternal.getName())).domainName(domainName).build());
					
					Game game = Game.builder()
							.providerGuid(moduleInfo.getModuleName().toLowerCase())
							.providerGameId(gameInternal.getId())
							.name(getGameNameWithoutOs(gameInternal.getName()))
							.labels(labels)
							.build();
					gameList.add(game);
				}
			}
			
			
			return gameList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	@RequestMapping(path="/listFrbGames")
	public List<Game> listFrbGames(@PathVariable("domainName") String domainName) throws Exception {
		try {
			BrandsConfigurationBrand bc = betsoftService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
			
			GamesSuitesInternal gameListInternal = restTemplate.postForObject(bc.getBaseUrl()+"/frbgamelist.do?bankId="+bc.getBankId(), null, GamesSuitesInternal.class);
			//GamesSuitesInternal gameListInternal = restTemplate.postForObject("https://lobby-ffp.discreetgaming.com/gamelist.do?bankId=936", null, GamesSuitesInternal.class);

			List<Game> gameList = new ArrayList<Game>();
			for(SuiteInternal suite : gameListInternal.getSuiteList()) {
				for(GameInternal gameInternal : suite.getGameList()) {
					
					Game game = Game.builder()
							.providerGuid(moduleInfo.getModuleName().toLowerCase())
							.providerGameId(gameInternal.getId())
							.name(getGameNameWithoutOs(gameInternal.getName()))
							.build();
					gameList.add(game);
				}
			}
			
			
			return gameList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
//	@RequestMapping(path="/listFrbGamesForUser")
//	public List<Game> listFrbGamesForUser(@PathVariable("domainName") String domainName) throws Exception {
//		try {
//			RestTemplate restTemplate = new RestTemplate();
//			BrandsConfigurationBrand bc = betsoftService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
//			//Test list if betsoft is not available
//			//GamesSuitesInternal gameListInternal = restTemplate.getForObject("http://localhost:9800/scripts/controllers/dashboard/games/list/test/bslist.xml", GamesSuitesInternal.class);
//			
//			GamesSuitesInternal gameListInternal = restTemplate.postForObject(bc.getBaseUrl()+"/frbgamelist.do?bankId="+bc.getBankId(), null, GamesSuitesInternal.class);
//			//GamesSuitesInternal gameListInternal = restTemplate.postForObject("https://lobby-ffp.discreetgaming.com/gamelist.do?bankId=936", null, GamesSuitesInternal.class);
//
//			List<Game> gameList = new ArrayList<Game>();
//			for(SuiteInternal suite : gameListInternal.getSuiteList()) {
//				for(GameInternal gameInternal : suite.getGameList()) {
//					
//					Game game = Game.builder()
//							.providerGuid(moduleInfo.getModuleName().toLowerCase())
//							.providerGameId(gameInternal.getId())
//							.name(getGameNameWithoutOs(gameInternal.getName()))
//							.build();
//					gameList.add(game);
//				}
//			}
//			
//			
//			return gameList;
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return null;
//		}
//	}
	
	private String getOperatingSystem(String gameName) {
		for(String os : osList) {
			if (gameName.endsWith(os)) {
				return os;
			}
		}
		return osList.get(osList.size()-1);
	}
	
	private String getGameNameWithoutOs(String gameName) {
		for(String os : osList) {
			if (gameName.endsWith(os)) {
				return gameName.replace(os, "").trim();
			}
		}
		return gameName;
	}
	
	/**
	 * Helper: to provide an xml list of game ids with an active freeround bonus for a user
	 * </br></br>
	 * Example request: <strong>http://dev.playsafesa.com:8080/betsoft-endpoint-apadmin/2000/DEV/2001/gameidswithfrb?userId=3286008</strong>
	 * </br></br>
	 * <GAMES>
	 * 	<GAME ID="1"/>
	 * 	<GAME ID="2"/>
	 * </GAMES>
	 * 
	 * @param userId
	 * @param apiAuthentication
	 * @return
	 * @throws RestClientException
	 * @throws UnsupportedEncodingException
	 */
//TODO: add this in later. Not sure we want to use it in its current form
//	@RequestMapping(value = "/gameidswithfrb", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
//	public @ResponseBody SuiteInternal listOfGameIdsWithFrbForUser(@RequestParam String userId, APIAuthentication apiAuthentication) throws RestClientException, UnsupportedEncodingException {
//		SuiteInternal games = new SuiteInternal();
//		List<GameInternal> finalList = new ArrayList<GameInternal>();
//		
//		GetBonusInfoRequest request = new GetBonusInfoRequest(userId, apiAuthentication.getBrandConfiguration().getBankId());
//		request.setHash(request.calculateHash(apiAuthentication.getBrandConfiguration().getHashPassword()));
//		
//		GetBonusInfoRequestResponse response = restTemplate.getForObject(apiAuthentication.getBrandConfiguration().getBaseUrl() 
//				+ FRB_GET_BONUS_INFO_URL_EXT + setRequestParams(request.getParamMap()), GetBonusInfoRequestResponse.class, request.getParamMap());
//		
//		if (response.getResponse().getResult().equalsIgnoreCase(GetBonusInfoResponse.RESPONSE_SUCCESS)) {
//			if (response.getResponse().getBonus() != null) {
//				for (int i = 0; i < response.getResponse().getBonus().size(); i++) {
//					Bonus bonus = response.getResponse().getBonus().get(i);
//					String tempGames[] = bonus.getGameIds().split("\\|");
//					for (int k = 0; k < tempGames.length; k++) {
//						GameInternal game = new GameInternal();
//						game.setId(tempGames[k]);
//						finalList.add(game);
//					}
//				}
//			}
//		}
//		
//		games.setGameList(finalList);
//		
//		return games;
//	}
}
