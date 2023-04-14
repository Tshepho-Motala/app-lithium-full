package lithium.service.games.controllers;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.Domain;
import lithium.service.games.client.objects.GameStudio;
import lithium.service.games.client.objects.GameSupplier;
import lithium.service.games.client.objects.GameType;
import lithium.service.games.config.ServiceGamesConfigurationProperties;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameGraphic;
import lithium.service.games.data.entities.GameUserStatus;
import lithium.service.games.data.entities.User;
import lithium.service.games.data.objects.GameGraphicBasic;
import lithium.service.games.data.repositories.GameRepository;
import lithium.service.games.services.GameService;
import lithium.service.games.services.GameUserLockStatusService;
import lithium.service.games.services.GameUserStatusService;
import lithium.service.games.services.messagehandlers.objects.GameUserStatusRequest;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@EnableConfigurationProperties(ServiceGamesConfigurationProperties.class)
@Slf4j
@RestController
@RequestMapping("/games")
// no tight coupling between client and service (so no implementation or
// dependence on service-client
public class GamesController {

	@Autowired
	private GameRepository gameRepo;
	@Autowired
	private GameUserStatusService gameUserStatusService;
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private ModelMapper mapper;
	@Autowired
	private GameService gameService;
	@Autowired
	private GameUserLockStatusService gameUserLockStatusService;

	@GetMapping("/gameuserstatus")
	public List<GameUserStatus> test(
		@RequestParam("playerGuid") String playerGuid
	) throws Exception {
		return gameUserLockStatusService.gameUserStatus(GameUserStatusRequest.builder().playerGuid(playerGuid).build());
	}

	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	@RequestMapping("/saveGameGraphic")
	public Response<GameGraphic> saveGameGraphic(@RequestBody @Valid GameGraphicBasic gameGraphicBasic, BindingResult br) throws Exception {

		Response<GameGraphic> gameGraphicResponse = gameService.saveGameGraphic(gameGraphicBasic);

		return gameGraphicResponse;
	}

	@RequestMapping("/updateProviderGames")
	public Response<Boolean> updateProviderGames() throws Exception {
		ProviderClient pc = services.target(ProviderClient.class,"service-domain", true);

		Iterable<Provider> providerList = pc.listAllProvidersByType(ProviderConfig.ProviderType.CASINO.type()).getData();

		for(Provider p : providerList) {
			if (!p.getEnabled()) continue;

			GamesClient gc = null;
			try {
				gc = services.target(GamesClient.class, p.getUrl(), true);
			} catch (LithiumServiceClientFactoryException lce) {
				log.warn("Problem getting provider service to populate game list: " + p.getUrl());
			}
			if (gc == null) continue;
			List<lithium.service.games.client.objects.Game> providerGameList = null;
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
					(lithium.service.games.client.objects.Game g1, lithium.service.games.client.objects.Game g2)->
						g1.getName().compareTo(g2.getName()));

			for (lithium.service.games.client.objects.Game game : providerGameList) {
				//Always add provider games with default domain
				gameService.addGame(game, "default", true, true);
			}
		}
		return Response.<Boolean>builder().data(true).status(Status.OK).build();
	}


	@RequestMapping("/add")
	public Response<lithium.service.games.client.objects.Game> addGame(
			@RequestParam("providerGuid")String providerGuid,
			@RequestParam("providerGameId") String providerGameId,
			@RequestParam("gameName") String gameName,
			@RequestParam(name = "commercialName") String commercialName,
			@RequestParam("description") String description,
			@RequestParam(name = "supplierGameGuid", required = false) String supplierGameGuid,
			@RequestParam(name="moduleSupplierId", required = false) String moduleSupplierId,
			@RequestParam(name="rtp", required = false) BigDecimal rtp,
			@RequestParam(name="introductionDate", required = false) String introductionDate,
			@RequestParam(name="activeDate", required = false) String activeDate,
			@RequestParam(name="inactiveDate", required = false) String inactiveDate,
			@RequestParam(name="domainName", defaultValue="default") String domainName,
			@RequestParam(name="freeSpinEnabled", required = false) Boolean freeSpinEnabled,
			@RequestParam(name="casinoChipEnabled", required = false, defaultValue = "false") Boolean casinoChipEnabled,
			@RequestParam(name="instantRewardEnabled", required = false, defaultValue = "false") Boolean instantRewardEnabled,
			@RequestParam(name="instantRewardFreespinEnabled", required = false, defaultValue = "false") Boolean instantRewardFreespinEnabled,
			@RequestParam(name="freeSpinValueRequired", required = false) Boolean freeSpinValueRequired,
			@RequestParam(name="freeSpinPlayThroughEnabled", required = false) Boolean freeSpinPlayThroughEnabled,
			@RequestParam(name="progressiveJackpot", required = false) Boolean progressiveJackpot,
			@RequestParam(name="networkedJackpotPool", required = false) Boolean networkedJackpotPool,
			@RequestParam(name="localJackpotPool", required = false) Boolean localJackpotPool,
			@RequestParam(name="gameSupplierId", required = false) Long gameSupplierId,
			@RequestParam(name="primaryGameTypeId", required = false) Long primaryGameTypeId,
			@RequestParam(name="secondaryGameTypeId", required = false) Long secondaryGameTypeId,
			@RequestParam(name="freeGame", required = false, defaultValue = "false") Boolean freeGame,
			@RequestParam(name="liveCasino", required = false, defaultValue = "false") Boolean liveCasino,
			@RequestParam(name="gameStudioId", required = false) Long gameStudioId,
			@RequestParam(name = "supplierGameRewardGuid", required = false) String supplierGameRewardGuid
	) throws Exception {

		Date introductionDateAdd = null;
		Date activeDateAdd = null;
		Date inactiveDateAdd = null;
		if(introductionDate != null) {
			introductionDateAdd = getDateFromStringDate(introductionDate);
		}

		if(activeDate != null) {
			activeDateAdd = getDateFromStringDate(activeDate);
		} else if (activeDate == null && introductionDate != null) {
			activeDateAdd = introductionDateAdd;
		}

		if (inactiveDate != null) {
			inactiveDateAdd = getDateFromStringDate(inactiveDate);
		}


		lithium.service.games.client.objects.Game g = lithium.service.games.client.objects.Game.builder()
				.providerGuid(providerGuid)
				.providerGameId(providerGameId)
				.name(gameName)
				.commercialName(commercialName)
				.description(description)
				.supplierGameGuid(supplierGameGuid)
				.moduleSupplierId(moduleSupplierId)
				.rtp(rtp)
				.introductionDate(introductionDateAdd)
				.activeDate(activeDateAdd)
				.inactiveDate(inactiveDateAdd)
				.freeSpinEnabled(freeSpinEnabled)
				.freeSpinValueRequired(freeSpinValueRequired)
				.freeSpinPlayThroughEnabled(freeSpinPlayThroughEnabled)
				.casinoChipEnabled(casinoChipEnabled)
				.instantRewardEnabled(instantRewardEnabled)
				.instantRewardFreespinEnabled(instantRewardFreespinEnabled)
				.localJackpotPool(localJackpotPool)
				.networkedJackpotPool(networkedJackpotPool)
				.progressiveJackpot(progressiveJackpot)
				.gameSupplier(GameSupplier.builder().id(gameSupplierId).build())
				.primaryGameType(GameType.builder().id(primaryGameTypeId).build())
				.secondaryGameType(GameType.builder().id(secondaryGameTypeId).build())
				.freeGame(freeGame)
				.liveCasino(liveCasino)
				.gameStudio(GameStudio.builder().id(gameStudioId).build())
				.supplierGameRewardGuid(supplierGameRewardGuid)
				.build();

			Game dbGame = gameService.addGame(g, domainName, true, true);
			g.setId(dbGame.getId());
			g.setGuid(dbGame.getGuid());
			g.setDomain(mapper.map(dbGame.getDomain(), Domain.class));
			if (dbGame.getGameSupplier() != null) g.setGameSupplier(mapper.map(dbGame.getGameSupplier(), GameSupplier.class));
			if (dbGame.getPrimaryGameType() != null) g.setPrimaryGameType(mapper.map(dbGame.getPrimaryGameType(), GameType.class));
			if (dbGame.getSecondaryGameType() != null) g.setPrimaryGameType(mapper.map(dbGame.getSecondaryGameType(), GameType.class));
			if (dbGame.getGameStudio() != null) g.setGameStudio(mapper.map(dbGame.getGameStudio(), GameStudio.class));
			g.setRtp(dbGame.getRtp());
			g.setIntroductionDate(dbGame.getIntroductionDate());
			return Response
					.<lithium.service.games.client.objects.Game>builder()
					.data(g)
					.status(Status.OK)
					.build();
	}

	private Date getDateFromStringDate(String stringDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.parse(stringDate);
	}

	@RequestMapping("/{gameId}/findById")
	public Response<lithium.service.games.client.objects.Game> findById(@PathVariable("gameId") Long gameId) throws Exception {
		lithium.service.games.client.objects.Game game = gameService.getGame(gameId);
		if(game == null) {
			return Response.<lithium.service.games.client.objects.Game>builder().status(Status.NOT_FOUND).build();
		}
		return Response.<lithium.service.games.client.objects.Game>builder().data(game).status(Status.OK).build();
	}

	@RequestMapping("/edit")
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public Response<lithium.service.games.client.objects.Game> edit(
		@RequestBody @Valid lithium.service.games.client.objects.Game game,
		BindingResult br,LithiumTokenUtil tokenUtil
	) throws Exception {
		if (br != null && br.hasErrors()) {
			return Response.<lithium.service.games.client.objects.Game>builder().status(Status.INVALID_DATA).build();
		}
		return Response.<lithium.service.games.client.objects.Game>builder()
				.data(gameService.editGame(game, false, false, tokenUtil))
				.status(Status.OK).build();
	}

	@GetMapping("/list/{gameId}/userstatus")
	public DataTableResponse<GameUserStatus> listGameUserStatus(
		@PathVariable("gameId") Game game,
		DataTableRequest request
	) throws Exception {
//		String searchValue = (request.getSearchValue()!=null)?request.getSearchValue():"";
		Page<GameUserStatus> findAllByGame = gameUserStatusService.findAllByGame(game, request.getPageRequest());

		return new DataTableResponse<GameUserStatus>(request, findAllByGame);
	}

	@PostMapping("/{gameId}/unlock/toggle")
	public Response<GameUserStatus> toggleLocked(
		@PathVariable("gameId") Game game,
		@RequestBody User user
	) {
		GameUserStatus gus = gameUserStatusService.toggleLocked(game, user);

		return Response.<GameUserStatus>builder().data(gus).status(Status.OK).build();
	}

	@PostMapping("/{gameGuid}/unlock")
	public Response<GameUserStatus> unlockGame(
				@PathVariable("gameGuid") String gameGuid,
				@RequestBody User user
	) {
		GameUserStatus gus = gameUserStatusService.unlock(gameGuid, user);
		return Response.<GameUserStatus>builder().data(gus).status(Status.OK).build();
	}

	@DeleteMapping("/{gameId}/unlock/d")
	public Response<?> toggleDelete(
		@PathVariable("gameId") Game game,
		@RequestParam("guid") String userGuid
	) {
		gameUserStatusService.toggleDelete(game, userGuid);

		return Response.<String>builder().status(Status.OK).build();
	}

	@RequestMapping(value="/editGraphic", method=RequestMethod.POST, consumes="multipart/form-data")
	public Response<GameGraphic> editGraphic(
		@RequestParam("gameId") Long gameId,
		@RequestParam("graphicFunctionName") String graphicFunctionName,
		@RequestParam("deleted") boolean deleted,
		@RequestParam("enabled") boolean enabled,
		@RequestParam("domainName") String domainName,
		@RequestParam("image") MultipartFile file
	) throws Exception {
//		Game g = gameRepo.findOne(gameId);

		GameGraphicBasic gameGraphicBasic = GameGraphicBasic.builder()
				.deleted(deleted)
				.enabled(enabled)
				.gameId(gameId)
				.graphicFunctionName(graphicFunctionName.split(",")[0])
				.domainName(domainName.split(",")[0])
				.image(file.getBytes())
				.build();

		return saveGameGraphic(gameGraphicBasic, null);
	}

	@RequestMapping(value="/{gameId}/removeGraphic/{domainName}/{graphicFunction}/{liveCasino}", method=RequestMethod.POST)
	public Response<GameGraphic> removeGraphic(
		@PathVariable("gameId") Long gameId,
		@PathVariable("graphicFunction") String graphicFunction,
		@PathVariable("domainName") String domainName,
		@PathVariable("liveCasino") Boolean liveCasino
	) {
		return gameService.removeGameGraphic(gameId, domainName, graphicFunction, liveCasino);
	}

	@GetMapping("/{id}/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
		return gameService.changeLogs(id, p);
	}

}
