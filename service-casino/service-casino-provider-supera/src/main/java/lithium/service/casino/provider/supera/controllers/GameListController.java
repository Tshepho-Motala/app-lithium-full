package lithium.service.casino.provider.supera.controllers;

import lithium.service.casino.provider.supera.SuperaModuleInfo;
import lithium.service.casino.provider.supera.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.supera.data.GameInternal;
import lithium.service.casino.provider.supera.data.GameListInternal;
import lithium.service.casino.provider.supera.service.SuperaService;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.Label;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
	
	@Autowired protected SuperaService superaService;
	
	@Autowired protected SuperaModuleInfo moduleInfo;
	
	@Autowired protected RestTemplate rest;
	
	@Autowired protected ModelMapper mapper; 
	
	@RequestMapping(path="/listGames")
	public List<Game> listGames(@PathVariable("domainName") String domainName) throws Exception {
		try {
			
			BrandsConfigurationBrand bc = superaService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
			
			log.info("Game list url: " + bc.getGameListUrl());
			GameListInternal gameListInternal = rest.getForObject(bc.getGameListUrl(), GameListInternal.class);

			log.info(gameListInternal.toString());

			List<Game> gameList = new ArrayList<Game>();
			for(GameInternal gameInternal : gameListInternal.getGameList()) {
				HashMap<String, Label> labels = new HashMap<>();
				if(gameInternal.getPlatform() !=null) {
					labels.put("os", Label.builder().name("os").value(gameInternal.getPlatform()).domainName(domainName).build());
				}
				if(gameInternal.getCategory() != null) {
					labels.put("category", Label.builder().name("category").value(gameInternal.getCategory()).domainName(domainName).build());
				}
				
				Game game = Game.builder()
						.providerGameId(gameInternal.getId())
						.providerGuid(moduleInfo.getModuleName())
						.name(gameInternal.getName())
						.labels(labels)
						.build();
				gameList.add(game);
			}
			
			return gameList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	//This method is pointless for rival, since we control game list and can just add frb tag into game list
	@RequestMapping(path="/listFrbGames")
	public List<Game> listFrbGames(@PathVariable("domainName") String domainName) throws Exception {
		List<Game> gameList = new ArrayList<Game>();
		return gameList;
//		try {
//			RestTemplate restTemplate = new RestTemplate();
//			ProviderClient pc = services.target(ProviderClient.class,"service-domain", true);
//			String gameListUrl = "";
//			for(ProviderProperty p : pc.propertiesByProviderUrlAndDomainName(moduleInfo.getModuleName(), domainName).getData()) {
//				if(p.getName().equalsIgnoreCase(ConfigProperties.FRB_GAME_LIST_URL.getValue())) {
//					gameListUrl = p.getValue();
//				}
//			}
//			log.info("Frb Game list url: " + gameListUrl);
//			GameListInternal gameListInternal = restTemplate.getForObject(gameListUrl, GameListInternal.class);
//
//			log.info(gameListInternal.toString());
//
//			List<Game> gameList = new ArrayList<Game>();
//			for(GameInternal gameInternal : gameListInternal.getGameList()) {
//				HashMap<String, Label> labels = new HashMap<>();
//				labels.put("freeRoundBonusCapable", Label.builder().name("freeRoundBonusCapable").value("true").build());
//				
//				Game game = Game.builder()
//						.providerGameId(gameInternal.getId())
//						.providerGuid(moduleInfo.getModuleName())
//						.name(gameInternal.getName())
//						.labels(labels)
//						.build();
//				gameList.add(game);
//			}
//			
//			return gameList;
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return null;
//		}
	}
	
//	private String getOperatingSystem(String gameName) {
//		for(String os : osList) {
//			if (gameName.endsWith(os)) {
//				return os;
//			}
//		}
//		return null;
//	}
//	
//	private String getGameNameWithoutOs(String gameName) {
//		for(String os : osList) {
//			if (gameName.endsWith(os)) {
//				return gameName.replace(os, "").trim();
//			}
//		}
//		return gameName;
//	}
}
