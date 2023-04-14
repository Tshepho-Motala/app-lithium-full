package lithium.service.casino.provider.livedealer.controllers;

import lithium.service.casino.provider.livedealer.LivedealerModuleInfo;
import lithium.service.casino.provider.livedealer.LivedealerModuleInfo.ConfigProperties;
import lithium.service.casino.provider.livedealer.data.GameInternal;
import lithium.service.casino.provider.livedealer.data.GameListInternal;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
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
	
	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	protected LivedealerModuleInfo moduleInfo;
	
	@Autowired
	protected ModelMapper mapper; 
	
	@RequestMapping(path="/listGames")
	public List<Game> listGames(@PathVariable("domainName") String domainName) throws Exception {
		try {
			RestTemplate restTemplate = new RestTemplate();
			ProviderClient pc = services.target(ProviderClient.class,"service-domain", true);
			String gameListUrl = "";
			for(ProviderProperty p : pc.propertiesByProviderUrlAndDomainName(moduleInfo.getModuleName(), domainName).getData()) {
				if(p.getName().equalsIgnoreCase(ConfigProperties.GAME_LIST_URL.getValue())) {
					gameListUrl = p.getValue();
				}
			}
			log.info("Game list url: " + gameListUrl);
			GameListInternal gameListInternal = restTemplate.getForObject(gameListUrl, GameListInternal.class);

			log.info(gameListInternal.toString());

			List<Game> gameList = new ArrayList<Game>();
			for(GameInternal gameInternal : gameListInternal.getGameList()) {
				HashMap<String, Label> labels = new HashMap<>();
				labels.put("os", Label.builder().name("os").value(gameInternal.getPlatform()).domainName(domainName).build());
				labels.put("category", Label.builder().name("category").value(gameInternal.getCategory()).domainName(domainName).build());
				labels.put("options", Label.builder().name("options").value(gameInternal.getOptions()).domainName(domainName).build());
				
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
	
	//This method is pointless for livedealer, since we control game list and can just add frb tag into game list
	@RequestMapping(path="/listFrbGames")
	public List<Game> listFrbGames(@PathVariable("domainName") String domainName) throws Exception {
		List<Game> gameList = new ArrayList<Game>();
		return gameList;
	}
}
