package lithium.service.casino.provider.twowinpower.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.provider.twowinpower.TwoWinPowerModuleInfo;
import lithium.service.casino.provider.twowinpower.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.twowinpower.config.ConfigurationPropertiesTWP;
import lithium.service.casino.provider.twowinpower.data.Game;
import lithium.service.casino.provider.twowinpower.data.GameListResponse;
import lithium.service.casino.provider.twowinpower.data.Links;
import lithium.service.games.client.objects.GameGraphic;
import lithium.service.games.client.objects.GameStream;
import lithium.service.games.client.objects.Label;
import lithium.service.games.stream.GamesStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GamesUpdateService {
	@Autowired
	private GamesStream gamesStream;
	@Autowired
	private TwoWinPowerService twpService;
	
	@Autowired
	@Qualifier("lithium.service.casino.provider.twowinpower.resttemplate")
	private RestTemplate restTemplate;
	@Autowired
	protected TwoWinPowerModuleInfo moduleInfo;
	@Autowired
	private ConfigurationPropertiesTWP config;
	
	@Async
	public void updateGames(String domainName, Boolean downloadImages) throws Exception {
		log.info("Lets update a game async.");
		List<Game> games = queryGameListFromProvider("default");
		log.debug("ProviderGames :: "+games);
		List<GameGraphic> gameGraphics = buildServiceGamesGameGraphicList(games);
		List<GameStream> gameStreamList = buildServiceGamesGameList(games, gameGraphics);
		log.info("Final number of games being sent : "+gameStreamList.size());
		for (GameStream gameStream:gameStreamList) gamesStream.registerGame(gameStream);
		log.info("Finished updateGames!");
	}
	
	public List<GameGraphic> buildServiceGamesGameGraphicList(List<Game> games) {
		boolean download = (config.getDownloadImages()!=null)?config.getDownloadImages():false;
		List<GameGraphic> list = new ArrayList<>();
		for (Game game:games) {
			try {
				URL url = new URL(game.getImage());
				BufferedImage image = ImageIO.read(url);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "jpg", baos);
				baos.flush();
				byte[] imageInByte = baos.toByteArray();
				baos.close();
				if (download) {
//					String filename = config.getImageDownloadPath()+game.getName()+"_"+game.getUuid()+"_"+image.getHeight()+"_"+image.getWidth()+".jpg";
					String filename = config.getImageDownloadPath()+"/"+moduleInfo.getModuleName().toLowerCase()+"_"+game.getUuid()+"/thumb/thumb.jpg";
					log.info("Saving : "+filename);
					FileUtils.writeByteArrayToFile(
						new File(filename),
						imageInByte
					);
				}
				GameGraphic gameGraphic = GameGraphic.builder()
				.image(imageInByte)
				.enabled(true)
				.name(game.getName())
				.gameGuid(moduleInfo.getModuleName().toLowerCase()+"_"+game.getUuid())
				.graphicFunctionName("original")
				.build();
				log.trace(gameGraphic.toString());
				list.add(gameGraphic);
			} catch (Exception e) {
				log.error("404 :: "+game+" : "+e.getMessage());
			}
		}
		return list;
	}
	
	public List<lithium.service.games.client.objects.Game> getServiceGamesGameList(List<GameStream> gameStreamList) {
		List<lithium.service.games.client.objects.Game> gameList = new ArrayList<>();
		gameStreamList.forEach(gs -> gameList.add(gs.getGame()));
		return gameList;
	}
	
	public List<GameStream> buildServiceGamesGameList(List<Game> games, List<GameGraphic> gameGraphics) {
		List<GameStream> gameStreamList = new ArrayList<>();
		for (Game twpGame:games) {
			HashMap<String, Label> labels = new HashMap<>();
			labels.put("provider", Label.builder().name("provider").value(twpGame.getProvider().toLowerCase()).build());
			labels.put("technology", Label.builder().name("technology").value(twpGame.getTechnology().toLowerCase()).build());
			labels.put("lobby", Label.builder().name("lobby").value((twpGame.getHasLobby() == 1)+"").build());
			labels.put("category", Label.builder().name("category").value(twpGame.getType().toLowerCase()).build());
			if (twpGame.getIsMobile() == 1) {
				labels.put("os", Label.builder().name("os").value("Mobile").build());
			} else {
				labels.put("os", Label.builder().name("os").value("Desktop").build());
			}
			
			lithium.service.games.client.objects.Game game = lithium.service.games.client.objects.Game.builder()
				.providerGuid(moduleInfo.getModuleName().toLowerCase())
				.providerGameId(twpGame.getUuid())
				.name(twpGame.getName())
				.labels(labels)
				.build();
			
			GameStream gameStream = GameStream.builder().game(game).build();
			if (gameGraphics!=null)
			gameGraphics.parallelStream()
			.filter(gs -> gs.getGameGuid().equals(game.getProviderGuid()+"_"+game.getProviderGameId()))
			.findFirst().ifPresent(gs -> gameStream.setGraphic(gs));
			gameStreamList.add(gameStream);
		}
		return gameStreamList;
	}
	
	//TODO:
	public List<Game> queryGameListFromProvider(String domainName) {
		BrandsConfigurationBrand bc = twpService.getBrandConfiguration(moduleInfo.getModuleName(), domainName);
		
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("page", "1");
		
		HttpEntity<String> entity = new HttpEntity<>(twpService.buildHeaders(bc.getMerchantId(), bc.getMerchantKey()));
		ResponseEntity<GameListResponse> result = restTemplate.exchange(bc.getBaseUrl()+"/games/index", HttpMethod.GET, entity, GameListResponse.class);
		
		GameListResponse gameListResponse = result.getBody();
		List<Game> games = gameListResponse.getItems();
		
		Links links = gameListResponse.getLinks();
		log.info(""+gameListResponse.getMeta());
		log.info(""+links);
		boolean isLastPage = false;
		if (links.getSelf().equals(links.getLast())) {
			isLastPage = true;
		} else {
			try { Thread.sleep(1000); } catch (Exception e) {}
		}
		
		int pagesToLoad = (config.getGamePagesToLoad()!=null)?config.getGamePagesToLoad():result.getBody().getMeta().getPageCount();
		log.info("Loading : "+pagesToLoad+" page(s) of games.");
		while (!isLastPage) {
			if (links.getNext() == null) {
				isLastPage = true;
				log.info("No next page available.."+links);
				continue;
			}
			if (result.getBody().getMeta().getCurrentPage() == pagesToLoad) {
				isLastPage = true;
				log.info("Reached maximum pages to load. Stopping.");
				continue;
			}
			log.info("Going on to : "+links.getNext());
			parameters.set("page", ""+(result.getBody().getMeta().getCurrentPage()+1));
			HttpEntity<MultiValueMap<String, String>> entity2 = new HttpEntity<>(parameters, twpService.buildHeaders(bc.getMerchantId(), bc.getMerchantKey(), parameters.toSingleValueMap()));
			result = restTemplate.exchange(links.getNext().getHref(), HttpMethod.GET, entity2, GameListResponse.class, parameters);
			if (result.getStatusCodeValue() == 200) {
				log.info("Adding "+(result.getBody().getItems().size())+" games to gamelist ("+games.size()+")");
				games.addAll(result.getBody().getItems());
				links = result.getBody().getLinks();
				if (links.getSelf().equals(links.getLast())) {
					isLastPage = true;
					log.info("Last page reached.");
				}
				try { Thread.sleep(1000); } catch (Exception e) {}
			} else {
				log.warn("Something went wrong retrieving gamelist.."+result);
				isLastPage = true;
			}
		}
		return games;
	}
}
