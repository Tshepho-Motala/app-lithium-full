package lithium.service.games.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.leader.LeaderCandidate;
import lithium.metrics.LithiumMetricsService;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTablePostRequest;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.games.client.objects.ChannelMigrationJob;
import lithium.service.games.client.objects.GameDto;
import lithium.service.games.client.objects.GameSupplierDto;
import lithium.service.games.client.objects.TaggedGameBasic;
import lithium.service.games.config.ServiceGamesConfigurationProperties;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameCurrency;
import lithium.service.games.data.entities.GameGraphic;
import lithium.service.games.data.entities.GameLabelValue;
import lithium.service.games.data.entities.GameStudio;
import lithium.service.games.data.entities.GameSupplier;
import lithium.service.games.data.entities.GameType;
import lithium.service.games.data.entities.GameTypeEnum;
import lithium.service.games.data.entities.GameUserStatus;
import lithium.service.games.data.entities.Graphic;
import lithium.service.games.data.entities.GraphicFunction;
import lithium.service.games.data.entities.LabelValue;
import lithium.service.games.data.entities.User;
import lithium.service.games.enums.GameChannelsEnum;
import lithium.service.games.data.entities.progressivejackpotfeeds.projection.entities.GameProjection;
import lithium.service.games.data.entities.progressivejackpotfeeds.projection.repository.GameProjectionRepository;
import lithium.service.games.data.objects.GameGraphicBasic;
import lithium.service.games.data.repositories.DomainRepository;
import lithium.service.games.data.repositories.GameCurrencyRepository;
import lithium.service.games.data.repositories.GameGraphicRepository;
import lithium.service.games.data.repositories.GameLabelValueRepository;
import lithium.service.games.data.repositories.GameRepository;
import lithium.service.games.data.repositories.LabelRepository;
import lithium.service.games.data.specifications.GamesSpecification;
import lithium.service.games.exceptions.Status409DuplicateGroupException;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static lithium.service.games.enums.GameChannelsEnum.ANDROID_NATIVE;
import static lithium.service.games.enums.GameChannelsEnum.DESKTOP_WEB;
import static lithium.service.games.enums.GameChannelsEnum.MOBILE_IOS;
import static lithium.service.games.enums.GameChannelsEnum.MOBILE_WEB;

@Service
@Slf4j
public class GameService {
	
	@Autowired
	LabelRepository repository;
	@Autowired
	private LabelValueService labelValueService;
	@Autowired
	private GameLabelValueRepository gameLabelValueRepository;
	@Autowired
	private GameRepository gameRepo;
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private GraphicsService graphicsService;
	@Autowired
	private GameGraphicRepository gameGraphicRepo;
	@Autowired 
	private LithiumMetricsService metrics;
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private DomainService domainService;
	@Autowired
	private ApplicationContext appContext;
	@Autowired
	private ModelMapper mapper;
	@Autowired
	private GameUserStatusService gameUserStatusService;
	@Autowired
	private GameCurrencyRepository gameCurrencyRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private GameChannelService gameChannelService;
	@Autowired
	private GameSupplierService gameSupplierService;
	@Autowired
	private GameTypeService gameTypeService;
	@Autowired
	private GameStudioService gameStudioService;
	@Autowired
	private GameService self;
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	ServiceGamesConfigurationProperties properties;
	@Autowired
	CachingDomainClientService cachingDomainClientService;
	@Autowired
	LabelService labelService;
	@Autowired
	LeaderCandidate leaderCandidate;
	@Autowired
	GameProjectionRepository gameProjectionRepository;

	@Value("${lithium.service.games.blank-image-url:classpath:/images/200x200_blank.png}")
	private String blankImageUrl;

	public lithium.service.games.client.objects.Game editGame(
			lithium.service.games.client.objects.Game game,
			boolean modifyOnly,
			boolean addOnly,
			LithiumTokenUtil token
	) throws Exception {
		validateGame(game);
		//Disabling a game always sets the visible to false
		if (!game.isEnabled() && game.isVisible()) {
			game.setVisible(false);
		}
		if(game.isEnabled() && game.getSupplierGameGuid() != null) {
			checkDuplicateSupplierGuidEditGame(game.getGuid(), game.getSupplierGameGuid(), game.getDomain().getName());
		}
		Game dbGame = gameRepo.findByGuidAndDomainName(game.getGuid(), game.getDomain().getName());
		boolean isNew = false;
		if (dbGame == null) {
			dbGame = self.addGame(game, game.getDomain().getName(), modifyOnly, addOnly);
			isNew = true;
		}

		if (!isNew && game.isEnabled() != dbGame.isEnabled()) {
			if(!game.isEnabled()) {
				if(dbGame.getInactiveDate() != null) {
					game.setActiveDate(null);
				}
				if (game.getInactiveDate() == null || game.getInactiveDate().equals(dbGame.getInactiveDate()))
					game.setInactiveDate(new Date());
			}  else if (game.getActiveDate() == null && game.getInactiveDate() == null && game.getIntroductionDate() != null) {
				game.setActiveDate(game.getIntroductionDate());
			}
			else if(game.isEnabled() && (game.getActiveDate() == null || game.getActiveDate().equals(dbGame.getActiveDate()))) {
				game.setActiveDate(new Date());
			}
		}

		GameSupplier gameSupplier = null;
		if (game.getGameSupplier() != null && game.getGameSupplier().getId() != null) {
			gameSupplier = gameSupplierService.findOne(game.getGameSupplier().getId());
		}

		GameType primaryGameType = null;
		if (game.getPrimaryGameType() != null && game.getPrimaryGameType().getId() != null)
			primaryGameType = gameTypeService.findOne(game.getPrimaryGameType().getId());

		GameType secondaryGameType = null;
		if (game.getSecondaryGameType() != null && game.getSecondaryGameType().getId() != null)
			secondaryGameType = gameTypeService.findOne(game.getSecondaryGameType().getId());

		GameStudio gameStudio = null;
		if (game.getGameStudio() != null && game.getGameStudio().getId() != null) {
			gameStudio = gameStudioService.findOne(game.getGameStudio().getId());
		}


//		Game oldGame = mapper.map(dbGame, Game.class);
		Date gameIntroductionDateOld = dbGame.getIntroductionDate();
		Date activeDateOld = dbGame.getActiveDate();
		Date inactiveDateOld = dbGame.getInactiveDate();

		dbGame.setDescription(game.getDescription());
		if(game.getSupplierGameGuid() != null && game.getSupplierGameGuid().isBlank()){
			game.setSupplierGameGuid(null);
		}else {
			dbGame.setSupplierGameGuid(game.getSupplierGameGuid());
		}
		dbGame.setRtp(game.getRtp());
		dbGame.setIntroductionDate(game.getIntroductionDate());
		dbGame.setCommercialName(game.getCommercialName());
		dbGame.setActiveDate(game.getActiveDate());
		dbGame.setInactiveDate(game.getInactiveDate());
		dbGame.setName(game.getName());
		dbGame.setEnabled(game.isEnabled());
		dbGame.setExcludeRecentlyPlayed(game.isExcludeRecentlyPlayed());
		dbGame.setLocked(game.isLocked());
		dbGame.setLockedMessage(game.getLockedMessage());
		dbGame.setVisible(game.isVisible());
		dbGame.setFreeSpinEnabled(game.getFreeSpinEnabled());
		dbGame.setCasinoChipEnabled(game.getCasinoChipEnabled());
		dbGame.setInstantRewardEnabled(game.getInstantRewardEnabled());
		dbGame.setInstantRewardFreespinEnabled(game.getInstantRewardFreespinEnabled());
		dbGame.setFreeSpinValueRequired(game.getFreeSpinValueRequired());
		dbGame.setFreeSpinPlayThroughEnabled(game.getFreeSpinPlayThroughEnabled());
		dbGame.setProgressiveJackpot(game.getProgressiveJackpot());//new
		dbGame.setNetworkedJackpotPool(game.getNetworkedJackpotPool());//new
		dbGame.setLocalJackpotPool(game.getLocalJackpotPool());//new
		dbGame.setGameCurrency(self.saveGameCurrency(dbGame, game.getGameCurrency()));
		dbGame.setGameSupplier(gameSupplier);
		dbGame.setPrimaryGameType(primaryGameType);
		dbGame.setSecondaryGameType(secondaryGameType);
		dbGame.setFreeGame(game.getFreeGame());
		dbGame.setLiveCasino(game.getLiveCasino());
		dbGame.setGameStudio(gameStudio);
		dbGame.setModuleSupplierId(game.getModuleSupplierId());
		dbGame.setSupplierGameGuid(game.getSupplierGameGuid());
		dbGame.setSupplierGameRewardGuid(game.getSupplierGameRewardGuid());
		dbGame = gameRepo.save(dbGame);

		gameChannelService.toggleGameChannels(game.getChannels(), dbGame);

		log.debug("received for edit gameId: " + game.getId() + " labels:"+ game.getLabels().toString());
		//In new game creation labels are already added and no need to do it again
		if(!isNew) {
			self.labels(dbGame.getId(), self.getLabelsAsStringArrayFromHash(game.getLabels(), game.getDomain().getName()), false, false);
		}
		updateGameChangeLog(game, gameIntroductionDateOld, activeDateOld, inactiveDateOld, isNew, token);

		return game;
	}

	public void checkDuplicateSupplierGuidEditGame(String gameGuid, String gameSupplierGameGuid, String domain) {
		if(gameSupplierGameGuid == null || gameSupplierGameGuid.isBlank()){
			return;
		}
		List<Game> foundGames = gameRepo.findByGuidNotAndSupplierGameGuidAndDomainNameAndEnabledTrue(gameGuid, gameSupplierGameGuid, domain);
		if(!foundGames.isEmpty()){
			throw new Status409DuplicateGroupException("Supplier Game Guid Already Exists");
		}
	}

	private void updateGameChangeLog(lithium.service.games.client.objects.Game game,
									 Date introductionDateOld, Date activeDateOld, Date inactiveDateOld,
									 boolean isNew, LithiumTokenUtil token) throws Status500InternalServerErrorException {
		if (token != null && !isNew) {

			List<String> changedFields = new ArrayList<>();
			if (!nullableDatesEqual(game.getIntroductionDate(), introductionDateOld)) changedFields.add("introductionDate");
			if (!nullableDatesEqual(game.getActiveDate(), activeDateOld)) changedFields.add("activeDate");
			if (!nullableDatesEqual(game.getInactiveDate(), inactiveDateOld)) changedFields.add("inactiveDate");

			if (!changedFields.isEmpty()) {
				try {
					String clType = "edit";
					List<ChangeLogFieldChange> clfc = changeLogService.copy(game, new Game(),
							changedFields.toArray(new String[changedFields.size()]));
					changeLogService.registerChangesForNotesWithFullNameAndDomain("game", clType, game.getId(), token.guid(), token,
							null, null, clfc, Category.SUPPORT, SubCategory.GAMES, 0, game.getDomain().getName());
				} catch (Exception e) {
					String msg = "Changelog registration for introduction date add failed";
					String trace = " [domainName=" + game.getDomain().getName()
							+ ", introductionDate=" + game.getIntroductionDate()
							+ ", activeDate=" + game.getActiveDate()
							+ ", inactiveDate=" + game.getInactiveDate()
							+ ", authorGuid=" + token.guid() + "] ";
					log.error(msg + trace + e.getMessage(), e);
					throw new Status500InternalServerErrorException(msg);
				}
			}
		}
	}

	private boolean nullableDatesEqual(Date dateA, Date dateB) {
		return dateA == dateB || (dateA != null && dateB != null && dateA.compareTo(dateB) == 0);
	}

	private void validateGame(lithium.service.games.client.objects.Game game) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateToday = sdf.format(new Date());

		//User must not be able to set future date
		if (game.getActiveDate() != null && sdf.format(game.getActiveDate()).compareTo(dateToday) > 0) {
			throw new Exception("You cannot set a future Date");
		}

		//User must not be able to set future date
		if (game.getInactiveDate() != null && sdf.format(game.getInactiveDate()).compareTo(dateToday) > 0) {
			throw new Exception("You cannot set a future Date");
		}

	}

	@Transactional(rollbackFor=Exception.class)
	public void labels(Long gameId, String[] labels, boolean updateOnly, boolean addOnly) {
		log.debug("gameId: " + gameId + " labels:"+ labels.toString());
		List<GameLabelValue> glvList = gameLabelValueRepository.findByGameId(gameId);
		log.debug("current labels for game: " + glvList);
		if (labels != null) {
			for (String labelAndValue: labels) {
				String[] labelAndValueSplit = labelAndValue.split("=");
				if (labelAndValueSplit.length == 2) {
					String label = labelAndValueSplit[0];
					String value = labelAndValueSplit[1];
					log.debug("Going to work on: " + label + " value: " + value);
					GameLabelValue glv = label(gameId, label, value, addOnly);
					log.debug("Completed label work on: " + glv);
					for (GameLabelValue glvTmp : glvList) {
						if (glvTmp.getId() == glv.getId()) {
							log.debug("found match for label, removing from delete list: current: " + glvTmp + " new: " + glv);
							glvList.remove(glvTmp);
							break;
						}
					}
				}
			}
		}
		
		if(!updateOnly) {
			log.debug("Removing labels from game: " + gameId + " labels: " + glvList);
			for(GameLabelValue glv: glvList) {
				glv.setDeleted(true);
				glv.setEnabled(false);
				gameLabelValueRepository.save(glv);
			}
		}
	}
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public GameLabelValue label(
			@PathVariable Long gameId, 
			@RequestParam String key, 
			@RequestParam String value, 
			boolean addOnly) {
		
		GameLabelValue currentGlv = gameLabelValueRepository.findByGameIdAndLabelValueLabelNameAndDeletedFalse(gameId, key);

		if(currentGlv != null) {
			if(currentGlv.getLabelValue().getValue().contentEquals(value) || addOnly) {
				return currentGlv;
			}
			
			currentGlv.setDeleted(true);
			currentGlv.setEnabled(false);
			currentGlv = gameLabelValueRepository.save(currentGlv);
		}
		
		LabelValue lv = labelValueService.findOrCreate(key, value);

		GameLabelValue glv = gameLabelValueRepository.findByGameIdAndLabelValueId(gameId, lv.getId());
		if(glv == null) {
			glv = GameLabelValue.builder()
					.gameId(gameId)
					.labelValue(lv)
					.build();
			
			gameLabelValueRepository.save(glv);
		} else if (glv.isDeleted()) {
			glv.setDeleted(false);
			glv.setEnabled(true);
			gameLabelValueRepository.save(glv);
		}
		return glv;
	}
	
	public String constructGameGuid(String providerGuid, String providerGameId) {
		return providerGuid+"_"+providerGameId;
	}
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public Game findOrCreateGame(
			String domainName, String providerGuid, String gameName, String commercialName,
			String providerGameId, String description, String supplierGameGuid, BigDecimal rtp,
			Date introductionDate,
			Date activeDate,
			Date inactiveDate,
			Boolean freeSpinEnabled,
			Boolean casinoChipEnabled,
			Boolean instantRewardEnabled,
			Boolean instantRewardFreespinEnabled,
			Boolean freeSpinValueRequired,
			Boolean freeSpinPlayThroughEnabled,
			Boolean progressiveJackpot,
			Boolean localJackpotPool,
			Boolean networkedJackpotPool,
			Long gameSupplierId,
			Long primaryGameTypeId,
			Long secondaryGameTypeId,
			Boolean freeGame,
			Boolean isEnabled,
			Boolean liveCasino,
			Long gameStudioId,
			String moduleSupplierId
	) throws Exception {
		String gameGuid = constructGameGuid(providerGuid, providerGameId);
		if(supplierGameGuid != null && supplierGameGuid.isBlank()){
			supplierGameGuid = null;
		}
		Game game = gameRepo.findByGuidAndDomainName(gameGuid, domainName);
		if(game == null) {
			lithium.service.games.data.entities.Domain domain = domainRepository.findOrCreateByName(domainName,
					() -> new lithium.service.games.data.entities.Domain());

			GameSupplier gameSupplier = null;
			if (gameSupplierId != null) {
				gameSupplier = gameSupplierService.findOne(gameSupplierId);
			}

			GameType primaryGameType = primaryGameTypeId != null ? gameTypeService.findOne(primaryGameTypeId) : null;
			GameType secondaryGameType = secondaryGameTypeId != null ? gameTypeService.findOne(secondaryGameTypeId) : null;;
			GameStudio gameStudio = gameStudioId != null ? gameStudioService.findOne(gameStudioId) : null;

			game = Game.builder()
					.providerGuid(providerGuid)
					.providerGameId(providerGameId)
					.name(gameName)
					.commercialName(commercialName)
					.guid(gameGuid)
					.enabled(isEnabled)
					.excludeRecentlyPlayed(false)
					.domain(domain)
					.visible(false)
					.description(description)
					.supplierGameGuid(supplierGameGuid)
					.rtp(rtp)
					.introductionDate(introductionDate)
					.activeDate(activeDate)
					.inactiveDate(inactiveDate)
					.freeSpinEnabled(freeSpinEnabled)
					.casinoChipEnabled(casinoChipEnabled)
					.instantRewardEnabled(instantRewardEnabled)
					.instantRewardFreespinEnabled(instantRewardFreespinEnabled)
					.freeSpinValueRequired(freeSpinValueRequired)
					.freeSpinPlayThroughEnabled(freeSpinPlayThroughEnabled)
					.progressiveJackpot(progressiveJackpot) // denotes that a game has a progressive jackpot
					.localJackpotPool(localJackpotPool) // denotes that a game is part of a local jackpot pool
					.networkedJackpotPool(networkedJackpotPool) // denotes that a game is part of a networked jackpot pool
					.gameSupplier(gameSupplier)
					.primaryGameType(primaryGameType)
					.secondaryGameType(secondaryGameType)
					.freeGame(freeGame)
					.liveCasino(liveCasino)
					.gameStudio(gameStudio)
					.moduleSupplierId(moduleSupplierId)
					.build();
			game = gameRepo.save(game);
		}
		return game;
	}
	
	public String[] getGameLabelValuesAsStringArray(Long gameId) throws Exception {
		ArrayList<String> labels = new ArrayList<> ();
		List<GameLabelValue> glvList = gameLabelValueRepository.findByGameIdAndDeletedFalse(gameId);
		if (glvList == null || glvList.isEmpty()) {
			return null;
		}
		for(GameLabelValue glv :glvList) {
			if(glv.getLabelValue().getValue() != null) {
				labels.add(glv.getLabelValue().getLabel().getName()+"="+glv.getLabelValue().getValue());
			}
		}
		return labels.toArray(new String[labels.size()]);
	}
	
	public void findOrCreateGameGraphic() {
		//TODO: stub create graphic
	}
	
	public void modifyGameGraphic() {
		//TODO: stub modify game graphic
	}

	public Response<GameGraphic> saveGameGraphic(GameGraphicBasic gameGraphicBasic) throws Exception {
		
		Game game = gameRepo.findById(gameGraphicBasic.getGameId());
		
		if (game == null || !(game.getDomain().getName().contentEquals(gameGraphicBasic.getDomainName()))) {
			return Response.<GameGraphic>builder().status(Status.NOT_FOUND).build();
		}
		
		GraphicFunction graphicsFunction = graphicsService.findOrCreateGraphicFunction(gameGraphicBasic.getGraphicFunctionName());
		
		GameGraphic gameGraphic = 
			gameGraphicRepo.findByGameIdAndGraphicFunctionIdAndEnabledTrueAndDeletedFalse(game.getId(), graphicsFunction.getId());
		
		//look for graphic in ancestry
		if (gameGraphic == null) {
			gameGraphic = findFirstAncestralGameGraphic(game, graphicsFunction.getId());
		}
		
		//Check if image is really different
		if (gameGraphic != null) {
			if (graphicsService.isGraphicContentEqual(gameGraphic.getGraphic(), gameGraphicBasic.getImage())) {
				//Handle case where only enable and deleted got changed
				if (gameGraphic.getGameId() == gameGraphicBasic.getGameId()) {
					gameGraphic.setDeleted(gameGraphicBasic.isDeleted());
					gameGraphic.setEnabled(gameGraphicBasic.isEnabled());
					gameGraphic = gameGraphicRepo.save(gameGraphic);
				} else {
					gameGraphic = gameGraphic.toBuilder()
					.id(null)
					.gameId(gameGraphicBasic.getGameId())
					.deleted(gameGraphicBasic.isDeleted())
					.enabled(gameGraphicBasic.isEnabled())
					.build();
					gameGraphic = gameGraphicRepo.save(gameGraphic);
				}
				return Response.<GameGraphic>builder().data(gameGraphic).status(Status.OK).build();
			} else {
				//Found a graphic but it is different and it is not an ancestor
				if (gameGraphic.getGameId() == gameGraphicBasic.getGameId()) {
					gameGraphic.setDeleted(true);
					gameGraphic.setEnabled(false);
					gameGraphic = gameGraphicRepo.save(gameGraphic);
				}
			}
		}
		
		//Nothing found, write new game graphic
		Graphic graphic = graphicsService.saveGraphic(gameGraphicBasic.getImage());
		gameGraphic = GameGraphic.builder()
				.deleted(gameGraphicBasic.isDeleted())
				.enabled(gameGraphicBasic.isEnabled())
				.gameId(gameGraphicBasic.getGameId())
				.graphic(graphic)
				.graphicFunction(graphicsFunction)
				.build();
		gameGraphic = gameGraphicRepo.save(gameGraphic);
		
		if(!graphicSanityCheck(gameGraphic)) {
			return Response.<GameGraphic>builder().status(Status.CONFLICT).build();
		}
		return Response.<GameGraphic>builder().data(gameGraphic).status(Status.OK).build();
	}
	
	public Response<GameGraphic> removeGameGraphic(Long gameId, String domainName, String graphicFunction, Boolean liveCasino) {
		Game game = gameRepo.findOne(gameId);
		
		if (game == null || !(game.getDomain().getName().contentEquals(domainName))) {
			return Response.<GameGraphic>builder().status(Status.NOT_FOUND).build();
		}
		
		GraphicFunction graphicsFunction = graphicsService.findOrCreateGraphicFunction(graphicFunction);
		
		GameGraphic gameGraphic = gameGraphicRepo.findByGameIdAndEnabledTrueAndDeletedFalseAndLiveCasinoAndGraphicFunctionId(game.getId(), liveCasino, graphicsFunction.getId());
		if (gameGraphic != null) {
			gameGraphic.setDeleted(true);
			gameGraphic.setEnabled(false);
			gameGraphicRepo.save(gameGraphic);
		}
		
		return Response.<GameGraphic>builder().status(Status.OK).data(gameGraphic).build();
	}

	public GameGraphic findCdnExternalGraphic(String domainName, Long gameId, Boolean liveCasino)
			throws Status500InternalServerErrorException {
		Game game = gameRepo.findOne(gameId);
		DomainValidationUtil.validate(domainName, game.getDomain().getName());
		GraphicFunction graphicFunction = graphicsService.findOrCreateGraphicFunction(
				GraphicsService.GRAPHIC_FUNCTION_CDN_EXTERNAL);
		return gameGraphicRepo.findByGameIdAndEnabledTrueAndDeletedFalseAndLiveCasinoAndGraphicFunctionId(game.getId(), liveCasino, graphicFunction.getId());
	}

	public GameGraphic saveCdnExternalGraphic(String domainName, Long gameId, String url, Boolean liveCasino)
			throws Status500InternalServerErrorException {
		GameGraphic gameGraphic = findCdnExternalGraphic(domainName, gameId, liveCasino);
		if (gameGraphic != null) {
			gameGraphic.setUrl(url);
			gameGraphic = gameGraphicRepo.save(gameGraphic);
		} else {
			Game game = gameRepo.findOne(gameId);
			DomainValidationUtil.validate(domainName, game.getDomain().getName());
			gameGraphic = gameGraphicRepo.save(
				GameGraphic.builder()
						.enabled(true)
						.deleted(false)
						.graphicFunction(graphicsService.findOrCreateGraphicFunction(
								GraphicsService.GRAPHIC_FUNCTION_CDN_EXTERNAL))
						.gameId(gameId)
						.url(url)
						.liveCasino(liveCasino)
						.build()
			);
		}
		return gameGraphic;
	}

	public void removeCdnExternalGraphic(String domainName, Long gameId, Boolean liveCasino)
			throws Status500InternalServerErrorException {
		GameGraphic gameGraphic = findCdnExternalGraphic(domainName, gameId, liveCasino);
		gameGraphicRepo.delete(gameGraphic);
	}
	
	@Synchronized
	private boolean graphicSanityCheck(GameGraphic gg) {
		try {
			gameGraphicRepo.findByGameIdAndGraphicFunctionIdAndEnabledTrueAndDeletedFalse(gg.getGameId(), gg.getGraphicFunction().getId());
			return true;
		} catch (Exception e) {
			log.error("Problem returning saved graphic, possible duplicate, removing", e);
			gg.setDeleted(true);
			gg.setEnabled(false);
			gameGraphicRepo.save(gg);
		}
		return false;
	}

	private GameGraphic findFirstAncestralGameGraphic(Game game, long graphicFunctionId) throws Exception {
		
		for(Game ancestorGame: findAncestralGames(game)) {
			GameGraphic gameGraphic = gameGraphicRepo
					.findByGameIdAndGraphicFunctionIdAndEnabledTrueAndDeletedFalse(ancestorGame.getId(), graphicFunctionId);
			
			if(gameGraphic != null) return gameGraphic;
		}
		
		return null;
	}

	private List<Game> findAncestralGames(Game game) throws Exception {
		return metrics.timer(log).time("findAncestralGames", (StopWatch sw) -> {
		List<Game> ancestralGames = new ArrayList<>();
		sw.start("getAncestralDomains");
		List<Domain> domains = domainService.findAncestralDomains(game.getDomain().getName());
		sw.stop();
		sw.start("getAncestralGames");
		for(Domain domain: domains) {
			Game ancestorGame = gameRepo.findByGuidAndDomainName(game.getGuid(), domain.getName());
			if(ancestorGame != null) {
				ancestralGames.add(ancestorGame);
			}
		}
		sw.stop();
		
		return ancestralGames;
		});
	}
	
	public Game addGame(lithium.service.games.client.objects.Game providerGame, String domainName, boolean updateOnly, boolean addOnly) throws Exception {
		Long primaryGameTypeId = (providerGame.getPrimaryGameType() != null) ? providerGame.getPrimaryGameType().getId() : null;
		Long secondaryGameTypeId = (providerGame.getSecondaryGameType() != null) ? providerGame.getSecondaryGameType().getId() : null;
		Long gameSupplierId = (providerGame.getGameSupplier() != null) ? providerGame.getGameSupplier().getId() : null;
		Long gameStudioId = (providerGame.getGameStudio() != null) ? providerGame.getGameStudio().getId() : null;

		Game game = findOrCreateGame(domainName,
				providerGame.getProviderGuid(),
				providerGame.getName(),
				providerGame.getCommercialName(),
				providerGame.getProviderGameId(),
				providerGame.getDescription(),
				providerGame.getSupplierGameGuid(),
				providerGame.getRtp(),
				providerGame.getIntroductionDate(),
				providerGame.getActiveDate(),
				providerGame.getInactiveDate(),
				providerGame.getFreeSpinEnabled(),
				providerGame.getCasinoChipEnabled(),
				providerGame.getInstantRewardEnabled(),
				providerGame.getInstantRewardFreespinEnabled(),
				providerGame.getFreeSpinValueRequired(),
				providerGame.getFreeSpinPlayThroughEnabled(),
				providerGame.getProgressiveJackpot(),
				providerGame.getLocalJackpotPool(),
				providerGame.getNetworkedJackpotPool(),
				gameSupplierId,
				primaryGameTypeId,
				secondaryGameTypeId,
				providerGame.getFreeGame(),
				providerGame.isEnabled(),
				providerGame.getLiveCasino(),
				gameStudioId,
				providerGame.getModuleSupplierId());

		game.setSupplierGameRewardGuid(providerGame.getSupplierGameRewardGuid());
		gameRepo.save(game);

		labels(game.getId(), getLabelsAsStringArrayFromHash(providerGame.getLabels(), domainName), updateOnly, addOnly);

		return game;
	}
	
	@Cacheable(cacheNames="lithium.service.games.services.getEffectiveLabels")
	public HashMap<String, lithium.service.games.client.objects.Label>getEffectiveLabels(Game game) throws Exception {
		return metrics.timer(log).time("getEffectiveLabelsTop", (StopWatch sw) -> {
		sw.start("domainGames");
		List<Game> domainGames = new ArrayList<>();
		//Add self to ancestral games at position zero. This will get labels for self at lowest level
		domainGames.add(0, game);
		sw.stop();
		
		return getEffectiveLabels(domainGames);
		});
	}
	

	public HashMap<String, lithium.service.games.client.objects.Label> getEffectiveLabels(List<Game> ancestralGames) throws Exception {
		//Break from recursion
		return metrics.timer(log).time("getEffectiveLabels", (StopWatch sw) -> {
			if(ancestralGames.isEmpty()) return new HashMap<String, lithium.service.games.client.objects.Label>();
			
			Game g = ancestralGames.remove(0);
			
			HashMap<String, lithium.service.games.client.objects.Label> labels = getEffectiveLabels(ancestralGames);
			sw.start("getLabelValues");
			//Return labels if none is set for current game level
			List<GameLabelValue> glvList = getGameLabelValues(g);
			sw.stop();
			if(glvList == null) return labels;
			
			//Insert or modify existing labels with current game level labels
			sw.start("manipulateLabelValues");
			for(GameLabelValue glv: glvList) {
				//Take out the old label if this lower level has such a label
				labels.remove(glv.getLabelValue().getLabel().getName());
	
				lithium.service.games.client.objects.Label label = lithium.service.games.client.objects.Label.builder()
						.deleted(glv.isDeleted())
						.domainName(g.getDomain().getName())
						.enabled(glv.isEnabled())
						.name(glv.getLabelValue().getLabel().getName())
						.value(glv.getLabelValue().getValue())
						.build();
				
				labels.put(label.getName(), label);
			}
			sw.stop();
			
			return labels;
		});
	}

	private List<GameLabelValue> getGameLabelValues(Game g) {
		return gameLabelValueRepository.findByGameIdAndDeletedFalse(g.getId());
	}

	public String[] getLabelsAsStringArrayFromHash(HashMap<String, lithium.service.games.client.objects.Label> labels, String leafdomainName) {
		ArrayList<String> resultLabels = new ArrayList<>();
		if(labels != null) {
			labels.values()
			.stream()
			.filter(label->{
				if(label == null) return false; 
				if(leafdomainName.contentEquals("default")) return true;
				//Log.info(label.toString());
				return label.getDomainName().contentEquals(leafdomainName) ? true : false;
			})
			.forEach(label->{resultLabels.add(label.getName()+"="+label.getValue());});
		}
		
		return resultLabels.toArray(new String[resultLabels.size()]);
	}
	
	public Game findByGameAndDomainName(String guid, String domainName) {
		return gameRepo.findByGuidAndDomainName(guid, domainName);
	}
	
	public List<Game> findDomainGames(String domainName) {

		List<Game> gameList = gameRepo.findAllByDomainName(domainName);

		if (gameList == null) gameList = new ArrayList<>();
		
		return gameList;
	}

	public List<Game> findDomainFreeGames(String domainName) {
		return gameRepo.findAllByDomainNameAndFreeGameTrue(domainName);
	}

	public byte[] getBlankImage() {
		return getImage(blankImageUrl);
	}
	
	public byte[] getImage(String url) {
		Resource r = appContext.getResource(url);
		byte[] blankImage = null;
		if(r.exists()) {
			try {
				blankImage = IOUtils.toByteArray(r.getInputStream());
			} catch (IOException e) {
				log.warn("Unable to read image from: " + r.getDescription());
			}
		}
		
		return blankImage;
	}
	
	//Eviction happens in service domain when provider properties are modified
	@Cacheable(cacheNames="lithium.service.games.services.getProviderProperties", unless="#result == null")
	public Iterable<ProviderProperty> getProviderProperties(String providerGuid, String domainName) {
		log.info("calling getProperties");
		ProviderClient pc = null;
		try {
			pc = services.target(ProviderClient.class, "service-domain", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Unable to get provider client for: " + providerGuid +" and domain: " + domainName);
		}
		
		if(pc == null) return null;
		
		return pc.propertiesByProviderUrlAndDomainName(providerGuid, domainName).getData();
	}
	

	@TimeThisMethod
	public lithium.service.games.client.objects.Game getResponseGame(Game game) {
		SW.start("mapGameEntityToGameObject");
		lithium.service.games.client.objects.Game respGame = mapper.map(game, lithium.service.games.client.objects.Game.class);
		SW.stop();
		try {
			SW.start("setGameChannels");
			respGame.setChannels(gameChannelService.getChannels(game));
			SW.stop();
			respGame.setLockedMessage(null);
			respGame.setLabels(getEffectiveLabels(game));
		} catch (Exception e) {
			log.error("No effective labels found for: " + game.toString(), e);
		}
		return respGame;
	}
	
	@Cacheable(cacheNames="lithium.service.games.services.getResponseGame", unless="#result == null")
	public List<lithium.service.games.client.objects.Game> getDomainGameList(String domainName) throws Exception {
		return domainGameListInternal(domainName, null,null, null, null);
	}

	@Cacheable(cacheNames="lithium.service.games.services.getDomainGameList", unless="#result == null")
	public List<lithium.service.games.client.objects.Game> getDomainGameListPerChannel(String domainName, String channel, Boolean enabled, Boolean visible) throws Exception {
		return domainGameListInternal(domainName, null, enabled, visible, channel);
	}

	public List<lithium.service.games.client.objects.Game> getDomainGameList(String domainName, Boolean freeSpinEnabled, Boolean enabled, Boolean visible, String channel) throws Exception {
		return domainGameListInternal(domainName, freeSpinEnabled, enabled, visible, channel);
	}

	public List<GameDto> searchGames(String domainName, String searchTerm, int size) throws Exception {
		List<GameDto> gameDtos = new ArrayList<>();
		if (searchTerm.trim().length() < 2){
			log.error("Search term length should be greater than 2 characters" + searchTerm);
			return gameDtos;
		}
		try {
			List<lithium.service.games.client.objects.Game> games = self.getDomainGameList(domainName);
			if (!games.isEmpty()) {
				List<lithium.service.games.client.objects.Game> gameList = games.stream().filter(game -> game.getName().
						toLowerCase().contains(searchTerm.toLowerCase())).limit(size).collect(Collectors.toList());
				for (lithium.service.games.client.objects.Game game : gameList) {
					GameDto gameDto = new GameDto();
					gameDto.setName(game.getName());
					gameDto.setDescription(game.getDescription());
					gameDto.setGuid(game.getGuid());
					gameDto.setCdnImageUrl(game.getCdnImageUrl());
					GameSupplierDto gameSupplierDto = new GameSupplierDto();
					if (game.getGameSupplier() != null) {
						gameSupplierDto.setName(game.getGameSupplier().getName());
					}
					gameDto.setGameSupplier(gameSupplierDto);
					gameDtos.add(gameDto);
				}
			}
			return gameDtos;
		} catch (Exception e) {
			log.error("No games found for: " + searchTerm, e);
		}
		return gameDtos;
	}

//	@Cacheable(cacheNames="lithium.service.games.services.getUserGameList", key="#playerGuid", unless="#result == null")
	public List<lithium.service.games.client.objects.Game> getUserGameList(
			String domainName,
			String playerGuid,
			List<lithium.service.games.client.objects.Game> domainGameList) throws Exception {

		User user = userService.findOrCreate(playerGuid);
		domainGameList.stream().forEach((lithium.service.games.client.objects.Game game) -> {
			GameUserStatus gus = gameUserStatusService.findOrNull(game.getGuid(), user);
			if (gus != null) {
				game.setLocked(gus.getLocked());
			}
		});
		return domainGameList;
	}
	
	public boolean isGameLockedForPlayer(String gameGuid, String playerGuid) throws Exception {
		User user = userService.findOrCreate(playerGuid);
		Game game = gameRepo.findByGuidAndDomainName(gameGuid, user.domainName());
		if (game.isLocked()) {
			GameUserStatus gus = gameUserStatusService.findOrNull(game.getGuid(), user);
			if (gus != null) {
				return gus.getLocked();
			} else {
				return true;
			}
		}
		return false;
	}
	
	private List<lithium.service.games.client.objects.Game> domainGameListInternal(String domainName, Boolean freeSpinEnabled, Boolean enabled, Boolean visible, String channel) throws Exception {
		List<lithium.service.games.client.objects.Game> gameList = new ArrayList<lithium.service.games.client.objects.Game>();
		return metrics.timer(log).time("listDomainGames", (StopWatch sw) -> {
			sw.start("domainGameExtract");
			List<Game> domainGameList = findDomainGames(domainName);
			sw.stop();

			domainGameList = filterGameList(domainGameList, freeSpinEnabled, enabled, visible, channel);

			sw.start("findLockGraphicFunction");
			GraphicFunction lockGraphicFunction = graphicsService.findOrCreateGraphicFunction("Lock");
			sw.stop();

			sw.start("findCdnGraphicFunction");
			GraphicFunction cdnGraphicFunction = graphicsService.findOrCreateGraphicFunction("CDN_EXTERNAL");
			sw.stop();

			sw.start("labels");
			domainGameList.stream().forEach(game->{
				lithium.service.games.client.objects.Game respGame = getResponseGame(game);
				GameGraphic lockGraphic = 
						gameGraphicRepo.findByGameIdAndEnabledTrueAndDeletedFalseAndLiveCasinoAndGraphicFunctionId(game.getId(), game.getLiveCasino(), lockGraphicFunction.getId());
				respGame.setHasLockImage(lockGraphic != null);
				GameGraphic externalCdnGraphic =
						gameGraphicRepo.findByGameIdAndEnabledTrueAndDeletedFalseAndLiveCasinoAndGraphicFunctionId(game.getId(), game.getLiveCasino(), cdnGraphicFunction.getId());
				if (externalCdnGraphic != null){
					respGame.setCdnImageUrl(externalCdnGraphic.getUrl());
				}
				gameList.add(respGame);
			});
			sw.stop();
			
			sw.start("Sorting list");
			List<lithium.service.games.client.objects.Game> tGameList = gameList.stream().sequential()
			.sorted((lithium.service.games.client.objects.Game a, lithium.service.games.client.objects.Game b) -> {
				if (a.getLabels().get("order") != null && b.getLabels().get("order") != null) {
					try {
						return (int) (Long.valueOf(a.getLabels().get("order").getValue())-Long.valueOf(b.getLabels().get("order").getValue()));
					} catch(NumberFormatException nfe) {
						log.error("Unable to sort: " + a.toString() + " and " + b.toString(), nfe);
						return 0;
					}
				} else {
					return a.getName().compareTo(b.getName());
				}
			})
			.collect(Collectors.toList());
			sw.stop();
			
			return tGameList;
		});
	}

	private List<Game> filterGameList(List<Game> domainGameList, Boolean freeSpinEnabled, Boolean enabled, Boolean visible, String channel) {
		if (enabled != null && visible != null) {
			domainGameList = domainGameList.stream()
					.filter(a -> enabled.equals(a.isEnabled() && visible.equals(a.isVisible())))
					.collect(Collectors.toList());
		}

		if (channel != null) {
			domainGameList = domainGameList.stream().filter(games -> games.getGameChannels().stream()
					.anyMatch(c -> c.getChannel().getName().contentEquals(channel))).collect(Collectors.toList());
		}

		if (enabled != null && enabled.booleanValue() == false) {
			domainGameList = domainGameList.stream().filter(a -> enabled.equals(a.isEnabled())).collect(Collectors.toList());
		}

		if (freeSpinEnabled != null) {
			domainGameList = domainGameList.stream().filter(
					//TODO: also allow when db value is Null for backwards compatibility
					p -> freeSpinEnabled.equals(p.getFreeSpinEnabled())
			).collect(Collectors.toList());
		}
		return domainGameList;
	}

	public GameCurrency saveGameCurrency(Game game, lithium.service.games.client.objects.GameCurrency gc) {
		if (gc != null) {
			if (game.getGameCurrency() == null) {
				GameCurrency gameCurrency = GameCurrency.builder()
				.game(game)
				.currencyCode(gc.getCurrencyCode())
				.minimumAmountCents(gc.getMinimumAmountCents())
				.build();
				return gameCurrencyRepository.save(gameCurrency);
			} else {
				return saveGameCurrency(game.getGameCurrency(), gc);
			}
		} else {
			if (game.getGameCurrency() != null) {
				Long id = game.getGameCurrency().getId();
				game.setGameCurrency(null);
				game = gameRepo.save(game);
				gameCurrencyRepository.deleteById(id);
			}
		}
		return null;
	}
	
	private GameCurrency saveGameCurrency(GameCurrency dbGc, lithium.service.games.client.objects.GameCurrency gc) {
		dbGc.setCurrencyCode(gc.getCurrencyCode());
		dbGc.setMinimumAmountCents(gc.getMinimumAmountCents());
		return gameCurrencyRepository.save(dbGc);
	}

	public List<TaggedGameBasic> getTaggedGames(String domainName, String labelName, List<String> labelValues, Boolean liveCasino, String channel, Boolean enabled) {
		return findDomainGamesForLabelWithLabelValues(domainName, labelName, labelValues).stream().filter(game -> liveCasino.equals(game.getLiveCasino()))
				.filter(activeChannel -> activeChannel.getGameChannels().stream().anyMatch(c -> c.getChannel().getName().contentEquals(channel)))
				.filter(enabledTaggedGames -> enabled.equals(enabledTaggedGames.isEnabled()))
				.map(game -> {
					GameGraphic gameGraphic = null;

					try {
						gameGraphic = findCdnExternalGraphic(game.getDomain().getName(), game.getId(), liveCasino);
					} catch (Status500InternalServerErrorException e) {
						log.error("Problem trying to retrieve game graphic for tagged game [game="+game+"] "
								+ e.getMessage(), e);
					}

					String supplierName = (game.getGameSupplier() != null) ? game.getGameSupplier().getName() : null;
					String gameStudioName = (game.getGameStudio() != null) ? game.getGameStudio().getName() : null;
					return TaggedGameBasic.builder()
							.gameId(game.getGuid())
							.gameName(game.getName())
							.supplierName(supplierName)
							.freeGame(game.getFreeGame())
							.gameStudioName((gameStudioName))
							.commercialGameName(game.getCommercialName())
							.image((gameGraphic != null) ? gameGraphic.getUrl() : null)
							.build();
				}).collect(Collectors.toList());
	}

	public List<Game> findDomainGamesForLabelWithLabelValues(String domainName, String labelName, List<String> labelValues) {
		return gameRepo.findDomainGamesForLabelWithLabelValues(domainName, labelName, labelValues,false);
	}

    public Game findFirstByGameType(GameType gameType) {
		return gameRepo.findFirstByPrimaryGameTypeOrSecondaryGameType(gameType, gameType);
    }

	public List<Game> findAllByGameType(Long gameTypeId) {
		GameType gameType = gameTypeService.findOne(gameTypeId);
		if (gameType == null) {
			return null;
		}
		if (gameType.getType().equals(GameTypeEnum.PRIMARY)) {
			return gameRepo.findAllByPrimaryGameType(gameType);
		}
		else if (gameType.getType().equals(GameTypeEnum.SECONDARY)) {
			return gameRepo.findAllBySecondaryGameType(gameType);
		}
		return new ArrayList<>();
	}

	public List<Game> getGamesForDomainAndProvider(String domainName, String providerGuid) {
		return gameRepo.findAllByDomainNameAndProviderGuidAndEnabledTrue(domainName, providerGuid);
	}
	public Game findFirstByGameStudio(GameStudio gameStudio) {
		return gameRepo.findFirstByGameStudio(gameStudio);
	}

	@TimeThisMethod
	@Transactional
	public void migrateOsLabelToChannelLabels(ChannelMigrationJob channelMigrationJob) {

		Iterable<Domain> allDomains = new ArrayList<>();
		try {
			Response<List<Domain>> allPlayerDomains = cachingDomainClientService.getDomainClient().findAllPlayerDomains();
			if (allPlayerDomains.isSuccessful() && allPlayerDomains.getData() != null) {
				allDomains = allPlayerDomains.getData();
			}
		} catch (Status550ServiceDomainClientException e) {
			log.error("Unable to retrieve all player domains during Channels Migration Job - Canceling job run");
			return;
		}

		// Runs for each player domain
		for (Domain domain : allDomains) {
			if (!domain.getEnabled().booleanValue()) {
				continue;
			}
			if (domain.getDeleted().booleanValue()) {
				continue;
			}

			log.info("Performing channel migration on domain: " + domain.getName());
			List<Game> domainGames = findDomainGames(domain.getName());

			// We will iterate through all games, regardless whether they are enabled or visible
			for (Game game : domainGames) {
				log.info("Performing Channel Migration on game: " + game.getGuid() + ", domainName: " + domain.getName());

				List<String> channels = new ArrayList<>();
				List<GameLabelValue> gameLabelValues = getGameLabelValues(game);
				Optional<GameLabelValue> os = gameLabelValues.stream().filter(glv -> glv.getLabelValue().getLabel().getName().trim().equalsIgnoreCase("os")).findFirst();
				if (os.isPresent()) {
					GameLabelValue gameLabelValue = os.get();

					HashMap<String, String> valueToChannelConfigurer = channelMigrationJob.getValueToChannelConfigurer();
					for (Map.Entry<String, String> entry : valueToChannelConfigurer.entrySet()) {
						String labelValue = entry.getKey();
						String channelArray = entry.getValue();
						if (!gameLabelValue.isDeleted() && gameLabelValue.getLabelValue().getValue().equals(labelValue)) {
							log.info("Found matching value-to-channel-configurer value: " + labelValue + " on 'os' label | Adding channels: " + channelArray);
							String[] channel = channelArray.split("\\|");
							for (int i = 0; i < channel.length; i++) {
								GameChannelsEnum gameChannelsEnum = GameChannelsEnum.byName(channel[i].trim());
								switch (gameChannelsEnum) {
									case DESKTOP_WEB:
										channels.add(DESKTOP_WEB.gameChannelName);
										break;
									case MOBILE_WEB:
										channels.add(MOBILE_WEB.gameChannelName);
										break;
									case MOBILE_IOS:
										channels.add(MOBILE_IOS.gameChannelName);
										break;
									case ANDROID_NATIVE:
										channels.add(ANDROID_NATIVE.gameChannelName);
										break;
								}
							}
							if (channelMigrationJob.isRemoveOsGameLabelValue()) {
								gameLabelValue.setDeleted(true);
								gameLabelValue.setEnabled(false);
								GameLabelValue deletedGameLabelValue = gameLabelValueRepository.save(gameLabelValue);
								log.info("Deleting GameLabelValue 'os' after migrating to new GameChannel's | deleted gameLabelValue: " + deletedGameLabelValue);
							}
						}
					}
				}
				//If no OS label was found, then channels will be empty, therefore defaulting to configured default channels
				if (channels.isEmpty() && channelMigrationJob.isPersistDefaultGameChannels()) {
					channels = Arrays.stream(channelMigrationJob.getDefaultGameChannels().split("\\|")).collect(Collectors.toList());
					log.info("Did not find any OS label for gameGuid:" + game.getGuid() + ", defaultChannels: " + channelMigrationJob.getDefaultGameChannels());
				}

				if (channelMigrationJob.isPersistOsGameChannels()) {
					gameChannelService.toggleGameChannels(channels, game);
					log.info("Persisting new Channels for gameGuid: " + game.getGuid() + ", channel: " + DESKTOP_WEB + ", enabled: " + channels.stream().filter(s -> s.equals(DESKTOP_WEB)).findAny().isPresent());
					log.info("Persisting new Channels for gameGuid: " + game.getGuid() + ", channel: " + MOBILE_WEB + ", enabled: " + channels.stream().filter(s -> s.equals(MOBILE_WEB)).findAny().isPresent());
					log.info("Persisting new Channels for gameGuid: " + game.getGuid() + ", channel: " + MOBILE_IOS + ", enabled: " + channels.stream().filter(s -> s.equals(MOBILE_IOS)).findAny().isPresent());
					log.info("Persisting new Channels for gameGuid: " + game.getGuid() + ", channel: " + ANDROID_NATIVE + ", enabled: " + channels.stream().filter(s -> s.equals(ANDROID_NATIVE)).findAny().isPresent());
					channels = new ArrayList<>();
				} else {
					log.info("Validating Channel to be configured for gameGuid: " + game.getGuid() + ", channel: " + DESKTOP_WEB + ", enabled: " + channels.stream().filter(s -> s.equals(DESKTOP_WEB)).findAny().isPresent());
					log.info("Validating Channel to be configured for gameGuid: " + game.getGuid() + ", channel: " + MOBILE_WEB + ", enabled: " + channels.stream().filter(s -> s.equals(MOBILE_WEB)).findAny().isPresent());
					log.info("Validating Channel to be configured for gameGuid: " + game.getGuid() + ", channel: " + MOBILE_IOS + ", enabled: " + channels.stream().filter(s -> s.equals(MOBILE_IOS)).findAny().isPresent());
					log.info("Validating Channel to be configured for gameGuid: " + game.getGuid() + ", channel: " + ANDROID_NATIVE + ", enabled: " + channels.stream().filter(s -> s.equals(ANDROID_NATIVE)).findAny().isPresent());
				}
			}
		}
	}

	public Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
		return changeLogService.listLimited(ChangeLogRequest.builder()
				.entityRecordId(id)
				.entities(new String[] { "game" })
				.page(p)
				.build()
		);
	}

	public lithium.service.games.client.objects.Game getGame(Long gameId) throws Exception {
		Game dbGame = gameRepo.findOne(gameId);
		if(dbGame == null) {
			return null;
		}
		lithium.service.games.client.objects.Game game = mapper.map(dbGame, lithium.service.games.client.objects.Game.class);
		game.setChannels(gameChannelService.getChannels(dbGame));
		game.setLabels(self.getEffectiveLabels(dbGame));
		return game;
	}


	public Page<Game> buildGameTable(
			DataTablePostRequest request, String domainName
	) throws Status550ServiceDomainClientException {
		String enabled = request.requestData("enabled");
		String freeGame = request.requestData("freeGame");
		String visible = request.requestData("visible");
		String progressiveJackpot = request.requestData("progressiveJackpot");
		String instantRewardEnabled = request.requestData("instantRewardEnabled");
		String liveCasinoEnabled = request.requestData("liveCasinoEnabled");
		String recentlyPlayed = request.requestData("recentlyPlayed");
		List<Long> gameSupplierIds = request.requestDataListOfLong("gameSuppliers");
		String[] gameProviders = request.requestDataArray("gameProviders");
		String id = request.requestData("id");

		final List<String> providerList = (gameProviders != null && gameProviders.length > 0)
				? Arrays.stream(gameProviders)
				.filter(p -> p != null && !p.trim().isEmpty())
				.collect(Collectors.toList())
				: null;

		Page<Game> games = null;

		lithium.service.games.data.entities.Domain domainRetreived = domainRepository.findByName(domainName);
		List<lithium.service.games.data.entities.Domain> listOfDomains = new ArrayList<>();
		listOfDomains.add(domainRetreived);
		Specification<Game> spec = Specification.where(GamesSpecification.domainIn(listOfDomains));
		spec = addToSpec(request.getSearchValue(), spec, GamesSpecification::any);
		spec = addToSpecLongList(gameSupplierIds, spec, GamesSpecification::gameSupplierIdsIn);
		spec = addToSpec(providerList, spec, GamesSpecification::providersIn);
		spec = addToSpec(enabled, spec, GamesSpecification::enabled);
		spec = addToSpec(freeGame, spec, GamesSpecification::freeGame);
		spec = addToSpec(visible, spec, GamesSpecification::visible);
		spec = addToSpec(progressiveJackpot, spec, GamesSpecification::progressiveJackpot);
		spec = addToSpec(instantRewardEnabled, spec, GamesSpecification::instantRewardEnabled);
		spec = addToSpec(liveCasinoEnabled, spec, GamesSpecification::liveCasinoEnabled);
		spec = addToSpec(recentlyPlayed, spec, GamesSpecification::recentlyPlayed);
		spec = addToSpec(id, spec, GamesSpecification::idStartsWith);

		games = gameRepo.findAllBy(spec, request.getPageRequest());

		return games;
	}

	private Specification<Game> addToSpec(final String aString, Specification<Game> spec, Function<String, Specification<Game>> predicateMethod) {
		if (aString != null && !aString.isEmpty()) {
			Specification<Game> localSpec = Specification.where(predicateMethod.apply(aString));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<Game> addToSpecLongList(final List<Long> aLongList, Specification<Game> spec,
												   Function<List<Long>, Specification<Game>> predicateMethod) {
		if (aLongList != null && !aLongList.isEmpty()) {
			Specification<Game> localSpec = Specification.where(predicateMethod.apply(aLongList));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<Game> addToSpec(final List<String> aString, Specification<Game> spec,
										   Function<List<String>, Specification<Game>> predicateMethod) {
		if (aString != null && !aString.isEmpty()) {
			Specification<Game> localSpec = Specification.where(predicateMethod.apply(aString));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	public List<Game> getByGuidsAndDomain(Set<String> guids, String domainName) {
		lithium.service.games.data.entities.Domain domain = domainRepository.findByName(domainName);
		if (domain != null) {
			List<Game> games = gameRepo.findAllByGuidInAndDomainId(guids, domain.getId());
			return games;
		}
		return new ArrayList<>();
	}

	public List<GameProjection> getAllEnabledProgressiveJackpotFeedsGamesBySupplier(GameSupplier gameSupplier,
			Boolean enabled, Boolean progressiveJackpot) {
		List<GameProjection> games = gameProjectionRepository.findByGameSupplierAndEnabledAndProgressiveJackpot(gameSupplier, enabled, progressiveJackpot);
		return games;
	}

	public Map<String, Game> getDomainGameMap(String domain){
		Map<String, Game> gameMap = new HashMap<>();
		gameRepo.findBySupplierGameGuidNotNullAndDomainName(domain)
				.stream().filter(game -> !game.getSupplierGameGuid().trim().isEmpty()).
				forEach(game -> gameMap.put(game.getSupplierGameGuid(), game));
		return gameMap;
	}
}
