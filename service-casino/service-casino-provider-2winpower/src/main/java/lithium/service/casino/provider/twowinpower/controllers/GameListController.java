package lithium.service.casino.provider.twowinpower.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.provider.twowinpower.data.Game;
import lithium.service.casino.provider.twowinpower.service.GamesUpdateService;
import lithium.service.games.client.objects.GameStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/games/{domainName}")
public class GameListController {
	@Autowired
	@Qualifier("lithium.service.casino.provider.twowinpower.resttemplate")
	private RestTemplate restTemplate;
	@Autowired
	private GamesUpdateService gamesUpdateService;
	
	@RequestMapping(path="/listGames")
	public List<lithium.service.games.client.objects.Game> listGames(
		@PathVariable("domainName") String domainName
	) throws Exception {
		try {
			List<Game> games = gamesUpdateService.queryGameListFromProvider(domainName);
			List<GameStream> gameStreamList = gamesUpdateService.buildServiceGamesGameList(games, null);
			log.info("Final number of games returned : "+gameStreamList.size());
			List<lithium.service.games.client.objects.Game> gameList = gamesUpdateService.getServiceGamesGameList(gameStreamList);
			return gameList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
//	@RequestMapping(path="/listFrbGames")
//	public List<lithium.service.games.client.objects.Game> listFrbGames(@PathVariable("domainName") String domainName) throws Exception {
//		try {
//			BrandsConfigurationBrand bc = twpService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
			
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
//			return gameList;
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return null;
//		}
//	}
	
//	@RequestMapping(path="/listFrbGamesForUser")
//	public List<Game> listFrbGamesForUser(@PathVariable("domainName") String domainName) throws Exception {
//		try {
//			RestTemplate restTemplate = new RestTemplate();
//			BrandsConfigurationBrand bc = twpService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
//			//Test list if nucleus is not available
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
}
