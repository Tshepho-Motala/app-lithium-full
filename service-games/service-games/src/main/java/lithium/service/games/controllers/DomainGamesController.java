package lithium.service.games.controllers;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.exceptions.Status468GameLockedException;
import lithium.service.games.client.exceptions.Status469DepositRequiredException;
import lithium.service.games.client.exceptions.Status502ProviderProcessingException;
import lithium.service.games.client.objects.DomainGameData;
import lithium.service.games.client.objects.Label;
import lithium.service.games.config.ServiceGamesConfigurationProperties;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameGraphic;
import lithium.service.games.data.entities.GameUserStatus;
import lithium.service.games.data.entities.GraphicFunction;
import lithium.service.games.data.objects.GameGraphicBasic;
import lithium.service.games.data.repositories.GameGraphicRepository;
import lithium.service.games.data.repositories.GameRepository;
import lithium.service.games.data.repositories.GraphicFunctionRepository;
import lithium.service.games.data.specifications.GamesSpecification;
import lithium.service.games.exceptions.Status406DisabledGameException;
import lithium.service.games.exceptions.Status406NoGamesEnabledException;
import lithium.service.games.services.CashierClientService;
import lithium.service.games.services.DomainService;
import lithium.service.games.services.GameService;
import lithium.service.games.services.GameUserStatusService;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status416PlayerPromotionsBlockedException;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

@EnableConfigurationProperties(ServiceGamesConfigurationProperties.class)
@Slf4j
@RestController
@RequestMapping("/games/{domainName}")
// no tight coupling between client and service (so no implementation or dependence on service-client
public class DomainGamesController {
	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private GameRepository gameRepo;
	@Autowired
	private GraphicFunctionRepository graphicFunctionRepo;
	@Autowired
	private GameGraphicRepository gameGraphicRepo;
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private GamesController gamesController;
	@Autowired
	private GameService gameService;
	@Autowired
	private ModelMapper mapper;
	@Autowired
	private DomainService domainService;
	@Autowired
	private UserApiInternalClientService userApiInternalClientService;
	@Autowired
	private GameUserStatusService gameUserStatusService;
	@Autowired
	LocaleContextProcessor localeContextProcessor;
	@Autowired
	private LimitInternalSystemService limitInternalSystemService;
	@Autowired
	private CashierClientService cashierClientService;
	@Autowired
	private MessageSource messageSource;

	@RequestMapping("/listProviderGames")
	public DataTableResponse<Game> listProviderGames(
		@PathVariable("domainName") String domainName,
		@RequestParam("providerGuid") String providerGuid,
		@RequestParam("labelValues") List<String> labelValues,
		@RequestParam(name="enabled", defaultValue="true") Boolean enabled,
		DataTableRequest request
	) throws Exception {
		String searchValue = (request.getSearchValue()!=null)?request.getSearchValue():"";
		log.debug(domainName+" "+providerGuid+" "+labelValues+" "+searchValue);
		
		Page<Game> list = gameRepo.findAll(GamesSpecification.searchAll(enabled, domainName, providerGuid, request.getSearchValue()), request.getPageRequest());
		list.forEach(g -> {
			try {
				g.setLabels(gameService.getEffectiveLabels(g));
			} catch (Exception e) {
				log.error("Could not determine labels for : "+g);
			}
		});
		
		return new DataTableResponse<Game>(request, list);
	}
	
	@RequestMapping("/listDomainGamesDT")
	public DataTableResponse<Game> listDomainGames(
		@PathVariable("domainName") String domainName,
		@RequestParam(name="enabled", defaultValue="true") Boolean enabled,
		DataTableRequest request
	) throws Exception {
		String searchValue = (request.getSearchValue()!=null)?request.getSearchValue():"";
		log.debug(domainName+" "+searchValue);
		
		//FIXME: The specification is always going to return all games, the OR clause on match all provider will cause a union on all elements.
		// going to do custom paging now anyway. Keep customers happy.
		//Page<Game> list = gameRepo.findAll(GamesSpecification.searchAll(enabled, domainName, "", searchValue), request.getPageRequest());
		List<Game> list = gameRepo.findAll(GamesSpecification.searchAll(enabled, domainName, "", searchValue));
		List<Game> filteredList = new ArrayList<>();
		list.forEach(g -> {
			try {
				g.setLabels(gameService.getEffectiveLabels(g));
				
				if (!searchValue.trim().isEmpty()) {
					if (g.getName().toLowerCase().contains(searchValue.toLowerCase())) {
						filteredList.add(g);
					} else if (g.getProviderGuid().toLowerCase().contains(searchValue.toLowerCase())) {
						filteredList.add(g);
					} else {
						//loop labels
						g.getLabels().forEach((key,value) -> {
							if (value.getValue().toLowerCase().contains(searchValue.toLowerCase())) {
								filteredList.add(g);
							}
						});
					}
				} else {
					filteredList.add(g);
				}
			} catch (Exception e) {
				log.error("Could not determine labels for : "+g);
			}
		});
		
		long start = request.getPageRequest().getOffset();
		long end = (start + request.getPageRequest().getPageSize()) > filteredList.size() ? filteredList.size() : (start + request.getPageRequest().getPageSize());
		Page<Game> page = new SimplePageImpl<>(filteredList.subList((int)start, (int)end), request.getPageRequest().getPageNumber(), request.getPageRequest().getPageSize(), filteredList.size());
		return new DataTableResponse<Game>(request, page);
	}
	
	@RequestMapping("/listDomainGamesReport")
	public DataTableResponse<Game> listDomainGamesReport(
		@PathVariable("domainName") String domainName,
		DataTableRequest request
	) throws Exception {
		String searchValue = (request.getSearchValue()!=null)?request.getSearchValue():"";
		log.debug(domainName+" "+searchValue);
		
		Page<Game> list = gameRepo.findAll(GamesSpecification.searchAll(domainName, "", searchValue), request.getPageRequest());
		list.forEach(g -> {
			try {
				g.setLabels(gameService.getEffectiveLabels(g));
			} catch (Exception e) {
				log.error("Could not determine labels for : "+g);
			}
		});
		
		return new DataTableResponse<Game>(request, list);
	}
	
	@RequestMapping("/listDomainGames")
	public Response<Iterable<lithium.service.games.client.objects.Game>> listDomainGames(
		@PathVariable("domainName") String domainName
	) throws Exception {
		return Response.<Iterable<lithium.service.games.client.objects.Game>>builder().data(gameService.getDomainGameList(domainName)).status(Status.OK).build();
	}

	@RequestMapping("/listDomainGamesPerChannel")
	public List<lithium.service.games.client.objects.Game> listDomainGamesPerChannel(
			@PathVariable("domainName") String domainName,
			@RequestParam("channel") String channel,
			@RequestParam(name = "enabled", required = true) Boolean enabled,
			@RequestParam(name = "visible", required = true) Boolean visible
	) throws Exception {
		List<lithium.service.games.client.objects.Game> domainGameListPerChannel =  gameService.getDomainGameListPerChannel(domainName, channel, enabled, visible);
		return domainGameListPerChannel;
	}
	
	@RequestMapping("/domainGameData")
	public Response<lithium.service.games.client.objects.DomainGameData> domainGameData(
		@PathVariable("domainName") String domainName,
		Principal principal
	) throws Exception {
		log.warn("Principal :"+principal);
		
		List<lithium.service.games.client.objects.Game> gameList = null;
		if (principal == null) {
			gameList = (List<lithium.service.games.client.objects.Game>) gameService.getDomainGameList(domainName);
		} else {
			LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
			log.warn("GameList Request for user : "+util.guid());
			gameList = (List<lithium.service.games.client.objects.Game>) gameService.getUserGameList(util.domainName(), util.guid(), gameService.getDomainGameList(domainName));
		}
		
		DomainGameData dgd = DomainGameData.builder()
		.lobbyOpenUrl("/openLobby?")
		.gameStartUrl("/startGame?token={TOKEN}&gameguid={GAMEGUID}&lang={LANG}&currency={CURRENCY}")
		.demoUrl("/demoGame?&gameguid={GAMEGUID}&lang={LANG}")
		.imageUrl("/getImage?gameguid={GAMEGUID}&function={IMAGEPURPOSE}")
		//Read the gamelist and filter out non-visible items
		.list(gameList.stream().filter((lithium.service.games.client.objects.Game game) ->{
			return game.isVisible();
		}).collect(Collectors.toList()))
		.build();
		return Response.<lithium.service.games.client.objects.DomainGameData>builder().data(dgd).status(Status.OK).build();
	}
	
	@RequestMapping("/getGameUrl")
	public Response<String> getGameUrl(
		@PathVariable("domainName") String domainName,
		@RequestParam(value="machineGUID", required=false) String machineGUID,
		@RequestParam("token") String token,
		@RequestParam("gameguid") String gameguid,
		@RequestParam("lang") String lang,
		@RequestParam("currency") String currency,
		@RequestParam(value="tutorial", required=false) Boolean tutorial,
		@RequestParam(value="platform", required=false) String platform
	) throws
			Status483PlayerCasinoNotAllowedException,
			Status500LimitInternalSystemClientException,
			Status502ProviderProcessingException,
			Status512ProviderNotConfiguredException,
			Status550ServiceDomainClientException,
			Status500InternalServerErrorException,
			Status416PlayerPromotionsBlockedException {
		try {
			localeContextProcessor.setLocaleContextHolder(lang, domainName);
			log.info("getGameUrl : " + domainName + "/" + gameguid);
			Game game = gameRepo.findByGuidAndDomainName(gameguid, domainName);
			LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, token).build();
			if (game != null) {
				if(game.getFreeGame()){
					limitInternalSystemService.checkPromotionsAllowed(tokenUtil.guid());
				}
				validate(domainName, game, tokenUtil);
				limitInternalSystemService.checkPlayerCasinoAllowed(tokenUtil.guid());
			}
			if (game != null && game.isEnabled()) {
				HashMap<String, Label> labels = gameService.getEffectiveLabels(game);
				Label osLabel = labels.get("os");
				String os = null;
				if (osLabel != null) os = osLabel.getValue();
				GamesClient gc = services.target(GamesClient.class, game.getProviderGuid(), true);
				String url = gc.startGame(
						domainName, token, game.getProviderGameId(), lang,
						currency, os, machineGUID, tutorial, platform
				).getData();
				return Response.<String>builder().data(url).status(OK).build();
			}
			return Response.<String>builder().data(null).status(NOT_FOUND).build();
		} catch (Status483PlayerCasinoNotAllowedException |
				Status500LimitInternalSystemClientException |
				Status502ProviderProcessingException |
				Status512ProviderNotConfiguredException |
				Status550ServiceDomainClientException |
				Status416PlayerPromotionsBlockedException |
				Status468GameLockedException |
				Status469DepositRequiredException e) {
			log.error("get-game-url typed exception domain ["+domainName+"] gameGuid ["+gameguid+"] " +
					"exception message : " + e.getMessage(), e);
			throw e;
		} catch(Exception | UserClientServiceFactoryException e) {
			log.error("get-game-url untyped exception domain ["+domainName+"] gameGuid ["+gameguid+"] " +
					"exception message : " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(e.getMessage(), e);
		}
	}

	private void validateGameUserStatus(String userGuid, Game game, LithiumTokenUtil tokenUtil) throws LithiumServiceClientFactoryException, Status483PlayerCasinoNotAllowedException, Status406NoGamesEnabledException {
		if (game.getFreeGame() == null || !game.getFreeGame()) return;
		List<GameUserStatus> gameUserStatusList = gameUserStatusService.findUnlockedFreeGamesForUser(userGuid, tokenUtil);
		if(gameUserStatusList == null || gameUserStatusList.isEmpty()) {
			String message;
			lithium.service.translate.client.objects.Domain transDomain = new lithium.service.translate.client.objects.Domain(game.getDomain().getName());
			if (Objects.nonNull(cashierClientService.getFirstDeposit(userGuid))) {
				 message = messageSource.getMessage("ERROR_DICTIONARY.GAMES.FREE_GAMES_LOCKED", new Object[]{transDomain}, LocaleContextHolder.getLocale());
				throw  new Status468GameLockedException(message);
			} else {
				message = messageSource.getMessage("ERROR_DICTIONARY.GAMES.DEPOSIT_REQUIRED", new Object[]{transDomain}, LocaleContextHolder.getLocale());
				throw  new Status469DepositRequiredException(message);
			}
		}
	}

	private void testAccountValidation(String domainName, String userGuid, Game game) throws Status483PlayerCasinoNotAllowedException, UserClientServiceFactoryException, UserNotFoundException {
		if(!domainService.casinoAllowTestAccountJackpotGames(domainName)) {
			if(userApiInternalClientService.isTestAccount(userGuid)) {
				if(game.getProgressiveJackpot()) {
					throw new Status483PlayerCasinoNotAllowedException();
				}
			}
		}
	}

	@RequestMapping("/startGame")
	public RedirectView startGame(
		@PathVariable("domainName") String domainName,
		@RequestParam(value="machineGUID", required=false) String machineGUID,
		@RequestParam("token") String token,
		@RequestParam("gameguid") String gameguid,
		@RequestParam("lang") String lang,
		@RequestParam("currency") String currency,
		@RequestParam(value="tutorial", required=false) Boolean tutorial,
		@RequestParam(value="platform", required=false) String platform
	) throws
			Status483PlayerCasinoNotAllowedException,
			Status500LimitInternalSystemClientException,
			Status502ProviderProcessingException,
			Status512ProviderNotConfiguredException,
			Status550ServiceDomainClientException,
			Status500InternalServerErrorException,
			Status416PlayerPromotionsBlockedException {
		try {
			localeContextProcessor.setLocaleContextHolder(lang, domainName);
			log.info("startGame : " + domainName + "/" + gameguid);
			Game game = gameRepo.findByGuidAndDomainName(gameguid, domainName);
			LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, token).build();
			if (game != null) {
				if(game.getFreeGame()){
					limitInternalSystemService.checkPromotionsAllowed(tokenUtil.guid());
				}
				validate(domainName, game, tokenUtil);
				HashMap<String, Label> labels = gameService.getEffectiveLabels(game);
				Label osLabel = labels.get("os");
				String os = null;
				if (osLabel != null) os = osLabel.getValue();
				GamesClient gc = services.target(GamesClient.class, game.getProviderGuid(), true);
				String url = gc.startGame(
						domainName, token, game.getProviderGameId(),
						lang, currency, os, machineGUID, tutorial, platform
				).getData();
				return new RedirectView(url);
			}
			return null;
		} catch (Status483PlayerCasinoNotAllowedException |
				 Status500LimitInternalSystemClientException |
				 Status502ProviderProcessingException |
				 Status512ProviderNotConfiguredException |
				 Status550ServiceDomainClientException |
				 Status416PlayerPromotionsBlockedException e) {
			log.error("start-game typed exception domain ["+domainName+"] gameGuid ["+gameguid+"] " +
					"exception message : " + e.getMessage(), e);
			throw e;
		} catch (Exception | UserClientServiceFactoryException e) {
			log.error("start-game untyped exception domain ["+domainName+"] gameGuid ["+gameguid+"] " +
					"exception message : " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(e.getMessage(), e);
		}
	}

	private void validate(String domainName, Game game, LithiumTokenUtil tokenUtil)
			throws Status483PlayerCasinoNotAllowedException,
			UserClientServiceFactoryException,
			LithiumServiceClientFactoryException,
			UserNotFoundException, Status406DisabledGameException, Status406NoGamesEnabledException {
		if (!game.isEnabled()) {
			throw new Status406DisabledGameException("Game Disabled");
		}
		if (game.isLocked()) {
			validateGameUserStatus(tokenUtil.guid(),game,tokenUtil);
		}
		testAccountValidation(domainName, tokenUtil.guid(), game);
	}

	@RequestMapping("/demoGame")
	public RedirectView demoGame(@PathVariable("domainName") String domainName, 
			@RequestParam("gameguid") String gameguid, 
			@RequestParam("lang") String lang, HttpServletResponse httpServletResponse
	) throws
			Status483PlayerCasinoNotAllowedException,
			Status500LimitInternalSystemClientException,
			Status502ProviderProcessingException,
			Status512ProviderNotConfiguredException,
			Status550ServiceDomainClientException,
			Status500InternalServerErrorException
	{
		try {
			Game game = gameRepo.findByGuidAndDomainName(gameguid, domainName);
			if(game != null) {

				HashMap<String,Label> labels = gameService.getEffectiveLabels(game);
				Label osLabel = labels.get("os");
				String os = null;
				if(osLabel != null)
					os = osLabel.getValue();

				GamesClient gc = services.target(GamesClient.class, game.getProviderGuid(), true);
				String url = gc.demoGame(domainName, game.getProviderGameId(), lang, os).getData();
				return new RedirectView(url);
			}
			return null;
		} catch (Status483PlayerCasinoNotAllowedException |
				Status500LimitInternalSystemClientException |
				Status502ProviderProcessingException |
				Status512ProviderNotConfiguredException |
				Status550ServiceDomainClientException e) {
			log.error("demo-game typed exception domain ["+domainName+"] gameGuid ["+gameguid+"] " +
					"exception message : " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("demo-game untyped exception domain ["+domainName+"] gameGuid ["+gameguid+"] " +
					"exception message : " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(e.getMessage(), e);
		}

	}

	@TimeThisMethod
	@GetMapping("/find/guid/{providerGuid}/{gameId}")
	public Response<lithium.service.games.client.objects.Game> findByGuidAndDomainName(
		@PathVariable("domainName") String domainName,
		@PathVariable("providerGuid") String providerGuid,
		@PathVariable("gameId") String gameId
	) throws Exception {
		return findByGuidAndDomainName(domainName, providerGuid+"_"+gameId);
	}

	@TimeThisMethod
	@RequestMapping("/find/guid/{gameGuid}")
	public Response<lithium.service.games.client.objects.Game> findByGuidAndDomainName(
		@PathVariable("domainName") String domainName,
		@PathVariable("gameGuid") String gameGuid
	) throws Exception {
		SW.start("domainGamesController.findByGuidAndDomainName.game");
		Game game = gameRepo.findByGuidAndDomainName(gameGuid, domainName);
		SW.stop();
		if (game == null) {
			return Response.<lithium.service.games.client.objects.Game>builder().status(NOT_FOUND).build();
		}
		lithium.service.games.client.objects.Game g = mapper.map(game, lithium.service.games.client.objects.Game.class);
		SW.start("domainGamesController.findByGuidAndDomainName.effectiveLabels");
		g.setLabels(gameService.getEffectiveLabels(game));
		SW.stop();
		return Response.<lithium.service.games.client.objects.Game>builder().status(OK).data(g).build();
	}

	@TimeThisMethod
	@RequestMapping("/find/guid/{gameGuid}/no-labels")
	public Response<lithium.service.games.client.objects.Game> findByGuidAndDomainNameNoLabels(
			@PathVariable("domainName") String domainName,
			@PathVariable("gameGuid") String gameGuid
	) throws Exception {
		SW.start("domainGamesController.findByGuidAndDomainNameNoLabels.game");
		Game game = gameRepo.findByGuidAndDomainName(gameGuid, domainName);
		SW.stop();
		if (game != null && !game.isEnabled()) {
			return Response.<lithium.service.games.client.objects.Game>builder().status(Status.DISABLED).build();
		}
		if (game == null) {
			return Response.<lithium.service.games.client.objects.Game>builder().status(NOT_FOUND).build();
		}
		lithium.service.games.client.objects.Game g = mapper.map(game, lithium.service.games.client.objects.Game.class);
		return Response.<lithium.service.games.client.objects.Game>builder().status(OK).data(g).build();
	}
	
	@GetMapping(value = "/getLockMessage") //, produces=MediaType.TEXT_HTML_VALUE)
	public Response<String> getLockMessage(
		@PathVariable("domainName") String domainName,
		@RequestParam("gameguid") String gameguid
	) throws Exception {
		Game game = gameRepo.findByGuidAndDomainName(gameguid, domainName);
		if (game != null) {
			return Response.<String>builder().status(OK).data(game.getLockedMessage()).build();
		}
		return Response.<String>builder().status(NOT_FOUND).data("").build();
	}
		
	
	//Handy image streaming examples at http://www.baeldung.com/spring-mvc-image-media-data
	@RequestMapping(value = "/getImage", method = RequestMethod.GET, produces=MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<byte[]> getImageAsResponseEntity(
		@PathVariable("domainName") String domainName,
		@RequestParam("gameguid") String gameguid,
		@RequestParam("function") String function,
		@RequestParam(value = "liveCasino", required = false, defaultValue = "false") Boolean liveCasino,
		@RequestParam(name="origin", required=false, defaultValue="website") String origin
	) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).noTransform().cachePublic().getHeaderValue());
		headers.setContentType(MediaType.IMAGE_PNG);
		
		Game defaultGame = gameRepo.findByGuidAndDomainName(gameguid, "default");
		Game game = gameRepo.findByGuidAndDomainName(gameguid, domainName);
		GraphicFunction gf = graphicFunctionRepo.findByName(function);
		GameGraphic gg = null;
		if (game != null && gf != null) {
			gg = gameGraphicRepo.findByGameIdAndEnabledTrueAndDeletedFalseAndLiveCasinoAndGraphicFunctionId(game.getId(), liveCasino, gf.getId());
			if ((gg == null) && ("website".equalsIgnoreCase(origin))) {
				gg = gameGraphicRepo.findByGameIdAndEnabledTrueAndDeletedFalseAndLiveCasinoAndGraphicFunctionId(defaultGame.getId(), liveCasino, gf.getId());
			}
		} else if (defaultGame != null && gf != null) {
			gg = gameGraphicRepo.findByGameIdAndEnabledTrueAndDeletedFalseAndLiveCasinoAndGraphicFunctionId(defaultGame.getId(), liveCasino, gf.getId());
		}
		if (gg != null) {
			log.debug("found graphic in DB returning: " + gameguid);
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(gg.getGraphic().getImage(), headers, HttpStatus.OK);
			return responseEntity;
		}
		
//		Game g = gameRepo.findByGuidAndDomainName(gameguid, domainName);
		String url = "";
		try {
			for (ProviderProperty p : gameService.getProviderProperties(game.getProviderGuid(), domainName)) {
				if (p.getName().equalsIgnoreCase("imageUrl")) {
					url += p.getValue();
				}
			}
		} catch (Throwable e) {
			log.error("ERROR ON TARGET INVOCATION game: "+game+" domain name: " + domainName, e);
		}
		url += "/"+gameguid+"/"+function+"/"+function+".png";
		log.info("url for image:" + url);
//		try {
			
		byte[] imageBytes = gameService.getImage(url);//restTemplate.getForObject(url, byte[].class);
			
		if (imageBytes != null) {
			GameGraphicBasic ggb = GameGraphicBasic.builder()
			.deleted(false)
			.enabled(true)
			.domainName("default")
			.gameId(defaultGame.getId())
			.graphicFunctionName(function)
			.image(imageBytes)
			.build();
			gamesController.saveGameGraphic(ggb, null);
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
			log.info("image haders: "+ responseEntity.getHeaders().toString());
			return responseEntity;
		}
	//	} catch (RestClientException rce) {
	//		if (rce instanceof org.springframework.web.client.HttpClientErrorException) {
	//			log.warn("Failed to get image from url ("+rce.getMessage()+"): " + url);
	//		} else {
	//			log.warn("Failed to get image from url ("+rce.getMessage()+"): " + url, rce);
	//		}
//		}
		byte[] blankImage = gameService.getBlankImage();
		if (blankImage != null) {
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(blankImage, headers, HttpStatus.OK);
			return responseEntity;
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@GetMapping("/isGameLockedForPlayer")
	public Response<Boolean> isGameLockedForPlayer(@RequestParam("gameGuid") String gameGuid, @RequestParam("playerGuid") String playerGuid) throws Exception {
		return Response.<Boolean>builder().status(OK).data(gameService.isGameLockedForPlayer(gameGuid, playerGuid)).build();
	}

	@RequestMapping("/updateProviderGames")
	public Response<Boolean> updateProviderGames(@PathVariable("domainName") String domainName) throws Exception {
		ProviderClient pc = services.target(ProviderClient.class,"service-domain", true);
		if (domainName == null)
			domainName = "default";

		Iterable<Provider> providerList = pc.listAllProvidersByType(ProviderConfig.ProviderType.CASINO.type()).getData();

		for(Provider p : providerList) {
			if (!p.getEnabled())  {
				log.warn("Provider is not enabled: " + p.toString());
				continue;
			}

			GamesClient gc = null;
			try {
				gc = services.target(GamesClient.class, p.getUrl(), true);
			} catch (LithiumServiceClientFactoryException lce) {
				log.warn("Problem getting provider service to populate game list: " + p.getUrl());
			}
			if (gc == null) continue;
			List<lithium.service.games.client.objects.Game> providerGameList;
			try {
				providerGameList = gc.listGames(p.getDomain().getName());
			} catch (HystrixRuntimeException e) {
				log.warn("Could not query :"+p.getName()+" :: "+e.getMessage());
				continue;
			}

			if (providerGameList == null) {
				log.warn("Provider game list is null: " + p.toString());
				continue;
			}
			//Sort alphabetically
			providerGameList.sort(
					Comparator.comparing(lithium.service.games.client.objects.Game::getName));

			for (lithium.service.games.client.objects.Game game : providerGameList) {
				gameService.addGame(game, domainName, true, true);
			}
		}
		return Response.<Boolean>builder().data(true).status(Status.OK).build();
	}

	@GetMapping("/get-domain-games-by-game-type")
	public Response<List<lithium.service.games.client.objects.Game>>  getDomainGamesByGameType(@RequestParam("gameTypeId") Long gameTypeId, @PathVariable("domainName") String domainName) {
		List<Game> gameList = gameService.findAllByGameType(gameTypeId);
		List<lithium.service.games.client.objects.Game> g = mapper.map(gameList, new TypeToken<List<lithium.service.games.client.objects.Game>>(){}.getType());
		return Response.<List<lithium.service.games.client.objects.Game>>builder().status(OK).data(g).build();
	}

}
