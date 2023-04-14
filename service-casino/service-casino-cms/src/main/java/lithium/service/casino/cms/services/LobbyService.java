package lithium.service.casino.cms.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status404LobbyConfigNotFoundException;
import lithium.exceptions.Status469InvalidInputException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance;
import lithium.service.casino.cms.api.objects.LobbyRequest;
import lithium.service.casino.cms.api.schema.lobby.AtoZWidget;
import lithium.service.casino.cms.api.schema.lobby.Banner;
import lithium.service.casino.cms.api.schema.lobby.BannerWidget;
import lithium.service.casino.cms.api.schema.lobby.DfgWidget;
import lithium.service.casino.cms.api.schema.lobby.GridWidget;
import lithium.service.casino.cms.api.schema.lobby.JackpotGridWidget;
import lithium.service.casino.cms.api.schema.lobby.LobbyResponse;
import lithium.service.casino.cms.api.schema.lobby.Tile;
import lithium.service.casino.cms.api.schema.lobby.TileWidget;
import lithium.service.casino.cms.api.schema.lobby.TopGamesWidget;
import lithium.service.casino.cms.api.schema.lobby.Widget;
import lithium.service.casino.cms.exceptions.Status404BannerNotFound;
import lithium.service.casino.cms.storage.entities.Domain;
import lithium.service.casino.cms.storage.entities.Lobby;
import lithium.service.casino.cms.storage.entities.LobbyRevision;
import lithium.service.casino.cms.storage.entities.PageBanner;
import lithium.service.casino.cms.storage.entities.User;
import lithium.service.casino.cms.storage.repositories.DomainRepository;
import lithium.service.casino.cms.storage.repositories.LobbyRepository;
import lithium.service.casino.cms.storage.repositories.LobbyRevisionRepository;
import lithium.service.casino.cms.storage.repositories.PageBannerRepository;
import lithium.service.casino.cms.storage.repositories.UserRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.games.client.GamesClient;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.games.client.ProgressiveJackpotFeedsClient;
import lithium.service.games.client.RecentlyPlayedGamesClient;
import lithium.service.games.client.RecommendedGamesClient;
import lithium.service.games.client.TaggedGamesClient;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.service.GamesInternalSystemClientService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.UserCategory;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class LobbyService {
	@Autowired private DomainRepository domainRepository;
	@Autowired private LithiumServiceClientFactory services;
	@Autowired private LobbyRepository repository;
	@Autowired private LobbyRevisionRepository revisionRepository;
	@Autowired private UserApiInternalClientService userApiInternalClientService;
	@Autowired private UserRepository userRepository;
	@Autowired private PageBannerRepository pageBannerRepository;
	@Autowired private GamesInternalSystemClientService gamesInternalSystemClientService;
	@Autowired LithiumServiceClientFactory serviceFactory;
	@Autowired private ProgressiveJackpotGameService progressiveJackpotGameService;

    public  void setLobbyRevisionUserData(List<lithium.service.user.client.objects.User> usersByGuids, List<LobbyRevision> content) {

		for (LobbyRevision lobbyRevision : content) {
			if (lobbyRevision.getCreatedBy()!=null) {
				lithium.service.user.client.objects.User createdBy = this.getUserByGuid(lobbyRevision.getCreatedBy().getGuid(), usersByGuids);
				lobbyRevision.getCreatedBy().setFullName(createdBy.getName());
			}
			if (lobbyRevision.getModifiedBy()!=null) {
				lithium.service.user.client.objects.User modifiedBy = this.getUserByGuid(lobbyRevision.getModifiedBy().getGuid(), usersByGuids);
				lobbyRevision.getModifiedBy().setFullName(modifiedBy.getName());
			}
		}

	}

	public static List<String> buildUserGuids(Page<LobbyRevision> revisionsByLobby) {
		List<String> guids =new ArrayList<>();
		for (LobbyRevision lobbyRevision : revisionsByLobby.getContent()) {
			guids.addAll(setGuidsByRevision(lobbyRevision));
		}
		List<String> uniqueGuids = guids.stream().distinct().collect(Collectors.toList());
		return uniqueGuids;
	}

	public static List<String> setGuidsByRevision(LobbyRevision lobbyRevision) {
		List<String> guids =new ArrayList<>();
		if (lobbyRevision.getCreatedBy()!=null) {
			guids.add(lobbyRevision.getCreatedBy().getGuid());
		}
		if (lobbyRevision.getModifiedBy()!=null) {
			guids.add(lobbyRevision.getModifiedBy().getGuid());
		}
		return guids;
	}

	private void setLobbiesUserData(Page<lithium.service.casino.cms.storage.entities.Lobby> lobbies) throws LithiumServiceClientFactoryException {
		List<LobbyRevision> lobbyRevisions = lobbies.getContent().stream()
				.filter(lobby -> lobby.getCurrent() != null)
				.map(lobby -> lobby.getCurrent()).collect(Collectors.toList());
		Set<String> userGuids = new HashSet<>();
		lobbyRevisions.stream().forEach(lobbyRevision -> {
			if(lobbyRevision.getCreatedBy() != null) {
				userGuids.add(lobbyRevision.getCreatedBy().getGuid());
			}
			if(lobbyRevision.getModifiedBy() != null) {
				userGuids.add(lobbyRevision.getModifiedBy().getGuid());
			}
		});
		List<lithium.service.user.client.objects.User> users = userApiInternalClientService.getUsers(new ArrayList<>(userGuids));
		setLobbyRevisionUserData(users, lobbyRevisions);
	}


	public boolean lobbyExists(String domainName) {
		return (repository.findTop1ByDomainName(domainName) != null);
	}

	public Page<Lobby> findLobbies(String domainName, Pageable pageable) throws LithiumServiceClientFactoryException {
		Page<Lobby> lobbies = repository.findByDomainName(domainName, pageable);
		if(lobbies != null && !lobbies.getContent().isEmpty()) {
			setLobbiesUserData(lobbies);
		}
		return lobbies;
	}

	@Transactional(rollbackOn = Exception.class)
	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 10, maxDelay = 500),
		exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
	public Lobby add(String domainName, String authorGuid, String description, String json)
			throws Status500InternalServerErrorException {
		if (lobbyExists(domainName)) {
			throw new Status500InternalServerErrorException("A lobby configuration for this domain already exists. " +
				"Modify the existing configuration.");
		}
		Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());
		User user = userRepository.findOrCreateByGuid(authorGuid, () -> new User());
		Lobby lobby = Lobby.builder()
			.domain(domain)
			.build();
		lobby = repository.save(lobby);
		LobbyRevision current = LobbyRevision.builder()
			.lobby(lobby)
			.createdBy(user)
			.description(description)
			.json(json)
			.build();
		current = revisionRepository.save(current);
		lobby.setCurrent(current);
		return repository.save(lobby);
	}

	public Page<LobbyRevision> findRevisionsByLobby(Lobby lobby, Pageable pageable) {
		return revisionRepository.findByLobby(lobby, pageable);
	}

	@Transactional(rollbackOn = Exception.class)
	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 10, maxDelay = 500),
		exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
	public Lobby modify(Lobby lobby, String editorGuid) {
		if (lobby.getEdit() == null) {
			LobbyRevision edit = LobbyRevision.builder().build();
			copy(lobby.getCurrent(), edit, editorGuid);
			lobby.setEdit(edit);
			lobby = repository.save(lobby);
		}
		return lobby;
	}

	@Transactional(rollbackOn = Exception.class)
	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 10, maxDelay = 500),
		exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
	public Lobby modify(Lobby lobby, LobbyRequest request, String editorGuid) {
		LobbyRevision edit = lobby.getEdit();
		edit.setDescription(request.getDescription());
		edit.setJson(request.getJson());
		edit.setModifiedBy(userRepository.findOrCreateByGuid(editorGuid, () -> new User()));
		edit = revisionRepository.save(edit);
		lobby.setEdit(edit);
		return repository.save(lobby);
	}

	public Lobby modifyAndSaveCurrent(Lobby lobby, LobbyRequest request, String editorGuid) {
		lobby = modify(lobby, request, editorGuid);
		lobby.setCurrent(lobby.getEdit());
		lobby.setEdit(null);
		return repository.save(lobby);
	}

	@TimeThisMethod
	public LobbyResponse getLobbyConfig(String brand, String channel, String primaryNavCode, String secondaryNavCode,
	        String userGuid) throws IOException, UserNotFoundException,
			UserClientServiceFactoryException, Status404LobbyConfigNotFoundException, LithiumServiceClientFactoryException, Status474DomainProviderDisabledException, Status550ServiceDomainClientException, Status500InternalServerErrorException {
		LobbyResponse config = null;

		Lobby lobby = repository.findTop1ByDomainName(brand);
		if (lobby == null) {
			throw new Status404LobbyConfigNotFoundException("Lobby configuration for " + brand + " not found.");
		}
 		List<LobbyResponse> channelConfigs = mapLobbyConfigsByChannel(lobby).get(channel);
		if (channelConfigs != null && !channelConfigs.isEmpty()) {
			for (LobbyResponse channelConfig: channelConfigs) {
				if (channelConfig.getPage().getPrimaryNavCode().contentEquals(primaryNavCode) &&
						channelConfig.getPage().getSecondaryNavCode().contentEquals(secondaryNavCode)) {
					config = channelConfig;
					break;
				}
			}
		}
		if(config == null) {
			String lobbyMessage =  "Lobby configuration for channel: " + channel + " and primary_nav_code: " + primaryNavCode +
					" and secondary_nav_code: " + secondaryNavCode + " for domain: " + brand + " not found";
			throw new Status404LobbyConfigNotFoundException(lobbyMessage);
		}
        Map<String, Set<String>> gameGuidProgressiveIdListMap = progressiveJackpotGameService.getGameGuidProgressiveIdListMap(brand);
		SW.start("populateDBGameData-" + brand + "-" + config.getPage().getPrimaryNavCode() + "-" + config.getPage().getSecondaryNavCode());
		populateDBGameData(config.getPage().getWidgets(), brand, gameGuidProgressiveIdListMap);
		SW.stop();
		SW.start("addBannersToLobby");
		addPageBanners(config, lobby.getId(), userGuid);
		SW.stop();
		Boolean liveCasino = isLiveCasino(primaryNavCode);
		if (secondaryNavCode.contentEquals("for_you")) {
			if (liveCasino != null) {
				addRecentlyPlayedGames(config, userGuid, liveCasino, channel, gameGuidProgressiveIdListMap);
				addRecommendedGames(config, brand, userGuid, liveCasino, channel);
			}
		}
		if (liveCasino != null) {
			addAllVisibleGames(config, liveCasino, channel, brand, gameGuidProgressiveIdListMap);
		}
		if (userGuid != null && liveCasino != null) {
			// TODO: Should it be on a specific secondary_nav_code, f.eg for_you, or, just put it on every page (current.)
			addTaggedGames(config, userGuid, liveCasino, channel, gameGuidProgressiveIdListMap);
		}
		return config;
	}

	private Boolean isLiveCasino(String primaryNavCode) {
		if (primaryNavCode.equals("live-casino")) {
			return  true;
		} else if (primaryNavCode.equals("casino")) {
			 return false;
		}
		return null;
	}

	@TimeThisMethod
	private void addRecommendedGames(LobbyResponse config, String domainName, String userGuid, boolean liveCasino, String channel) throws Status474DomainProviderDisabledException, LithiumServiceClientFactoryException, Status550ServiceDomainClientException, Status500InternalServerErrorException {
		log.debug("Fetching recommended games: domain: " + domainName + ", userguid: " + userGuid
				+ ", liveCasino: " + liveCasino + ", channel: " + channel + ", config: " + config);
		if (userGuid == null) {
			// userGuid not provided, use domainName/0 for global game recommendations
			userGuid = domainName + "/0";
		}
		if (!config.getPage().getWidgets().isEmpty()) {
			Widget recommendedGamesWidget = null;
			boolean recommendedTilesEmpty = true;
			for (Widget widget : config.getPage().getWidgets()) {
				if (widget instanceof TileWidget && ((TileWidget) widget).getTileWidgetType() != null) {
					boolean recommendedGamesWidgetExits = ((TileWidget) widget).getTileWidgetType().equalsIgnoreCase("recommendedGames");
					if (recommendedGamesWidgetExits) {
						recommendedGamesWidget = widget;
						try {
							List<Tile> recommendedGames = getRecommendedGamesClient().get()
									.getRecommendedGamesBasics(userGuid, liveCasino, channel, null)
									.stream().map(game -> {
										return Tile.builder()
												.type("game")
												.gameId(game.getGameId())
												.gameName(game.getGameName())
												.image(game.getImage())
												.gameRank(game.getGameRank())
												.build();
									})
									.collect(Collectors.toList());
							((TileWidget) widget).setTiles(recommendedGames);
							recommendedTilesEmpty = recommendedGames.isEmpty();
						} catch (Status474DomainProviderDisabledException | Status550ServiceDomainClientException e) {
							log.error("Recommended games provider is not configured for domain. domainName: " + domainName, e);
							break;
						} catch (Exception e) {
							String message = String.format("Failed to add recommended games for domain. domain: %s, userGuid: %s, liveCasino: %s, channel: %s",
									domainName, userGuid, liveCasino, channel);
							log.error(message, e);
							break;
						}
					}
				}

			}
			if (recommendedGamesWidget != null && recommendedTilesEmpty) {
				config.getPage().getWidgets().remove(recommendedGamesWidget);
			}
		}
	}

	private void addPageBanners(LobbyResponse config, Long lobbyId, String userGuid) {
		Boolean loggedIn = userGuid != null;
		String primaryNavCode = config.getPage().getPrimaryNavCode();
		String secondaryNavCode = config.getPage().getSecondaryNavCode();
		String channel = config.getPage().getChannel();
		Date now = new Date();
		List<PageBanner> pageBanners = pageBannerRepository.findAllVisibleBanners(primaryNavCode, secondaryNavCode, channel, lobbyId, loggedIn);
		if (pageBanners.size() > 0) {
			List<Banner> banners = pageBanners.stream().map(pageBanner -> {
				lithium.service.casino.cms.storage.entities.Banner banner = pageBanner.getBanner();

				return Banner.builder().image(banner.getImageUrl())
						.id(banner.getId())
						.name(banner.getName())
						.displayText(banner.getDisplayText())
						.runCount(pageBanner.getPosition())
						.termsUrl(banner.getTermsUrl())
						.url(banner.getLink())
						.build();
			}).collect(Collectors.toList());
			if (config.getPage().getWidgets().size() > 0 && config.getPage().getWidgets().get(0) instanceof BannerWidget) {
				((BannerWidget) config.getPage().getWidgets().get(0)).getBanners().addAll(banners);
				return;
			}
			BannerWidget bannerWidget = BannerWidget.builder()
					.type("banner")
					.banners(banners)
					.build();
			config.getPage().getWidgets().add(0, bannerWidget);
		}
	}

	@TimeThisMethod
	private void populateDBGameData(List<Widget> widgets, String domainName, Map<String, Set<String>> gameGuidProgressiveMap) throws LithiumServiceClientFactoryException {
		log.debug("Populate tiles with database data: domain: " + domainName + ", widgets: " + widgets);
		List<Tile> tiles = new ArrayList<>();
		for (Widget widget: widgets) {
			if (widget instanceof TileWidget) {
				tiles.addAll(((TileWidget) widget).getTiles());
			} else if (widget instanceof GridWidget) {
				tiles.addAll(((GridWidget) widget).getTiles());
			} else if (widget instanceof TopGamesWidget) {
				tiles.addAll(((TopGamesWidget) widget).getTiles());
			} else if (widget instanceof JackpotGridWidget) {
				tiles.addAll(((JackpotGridWidget) widget).getTiles());
			}
			else if (widget instanceof DfgWidget) {
				tiles.addAll(((DfgWidget) widget).getTiles());
			}

		}
		Set<String> gameIdList = tiles.stream().map(Tile::getGameId).collect(Collectors.toSet());

		Response<List<Game>> response = gamesInternalSystemClientService.getByGuidsAndDomain(domainName, gameIdList);
		if (response.isSuccessful() && !response.getData().isEmpty()) {
			Map<String, Game> gamesMap = response.getData().stream().collect(Collectors.toMap(Game::getGuid, Function.identity()));
			tiles.stream().forEach(tile -> {
				Game game = gamesMap.get(tile.getGameId());
				if (game != null) {
					if (game.getGameSupplier() != null) {
						tile.setSupplierName(game.getGameSupplier().getName());
					}
					if(game.getGameStudio() != null){
						tile.setGameStudioName(game.getGameStudio().getName());
					}
					tile.setFreeGame(game.getFreeGame());
					if (game.getProgressiveJackpot() && gameGuidProgressiveMap != null) {
						Set<String> progressiveJackpotIds = gameGuidProgressiveMap.get(game.getGuid());
						if (progressiveJackpotIds != null) {
							tile.setProgressiveIds(progressiveJackpotIds.stream().collect(Collectors.toList()));
						}
					}
				}
			});
		}
	}

	private void addRecentlyPlayedGames(LobbyResponse config, String userGuid, Boolean liveCasino, String channel, Map<String, Set<String>> gameGuidProgressiveMap) {
		if (userGuid == null) {
			return;
		}
		if (!config.getPage().getWidgets().isEmpty()) {
			for (Widget widget : config.getPage().getWidgets()) {
				if (widget instanceof TileWidget && ((TileWidget) widget).getTileWidgetType() != null) {
					boolean recentlyPlayedWidget = ((TileWidget) widget).getTileWidgetType().equalsIgnoreCase("recentlyPlayedGames");
					if (recentlyPlayedWidget) {
						List<Tile> recent = getRecentlyPlayedGamesClient().get()
								.recentlyPlayedGames(userGuid, liveCasino, channel)
								.stream().map(game -> {
									Tile tile = Tile.builder()
											.type("game")
											.gameId(game.getGameId())
											.gameName(game.getCommercialGameName())
											.freeGame(game.getFreeGame())
											.gameStudioName(game.getGameStudioName())
											.image(game.getImage())
											.build();
									if (gameGuidProgressiveMap != null) {
										Set<String> progressiveJackpotIds = gameGuidProgressiveMap.get(game.getGameId());
										if (progressiveJackpotIds != null) {
											tile.setProgressiveIds(progressiveJackpotIds.stream().collect(Collectors.toList()));
										}
									}
									return tile;
								})
								.collect(Collectors.toList());
						((TileWidget) widget).setTiles(recent);
					}
				}

			}
		}
	}

	private void addAllVisibleGames(LobbyResponse config, Boolean liveCasino, String channel, String domainName, Map<String, Set<String>> gameGuidProgressiveMap) {
		if (!config.getPage().getWidgets().isEmpty()) {
			try {
				for (Widget widget : config.getPage().getWidgets()) {
					if (widget instanceof AtoZWidget && ((AtoZWidget) widget).getTileWidgetType() != null) {
							List<Game> games = getGamesClient().get()
									.listDomainGamesPerChannel(domainName, channel, true, true);
							if (!games.isEmpty()) {
								games = games.stream().filter(game -> liveCasino.equals(game.getLiveCasino())).collect(Collectors.toList());
								List<Tile> tiles = games.stream().map(game -> {
									Tile tile = Tile.builder()
											.type("game")
											.gameId(game.getGuid())
											.gameName(game.getCommercialName())
											.supplierName(game.getGameSupplier() != null ? game.getGameSupplier().getName() : null)
											.freeGame(game.getFreeGame())
											.image(game.getCdnImageUrl())
											.build();
									if (gameGuidProgressiveMap != null) {
										Set<String> progressiveJackpotIds = gameGuidProgressiveMap.get(game.getGuid());
										if (progressiveJackpotIds != null) {
											tile.setProgressiveIds(progressiveJackpotIds.stream().collect(Collectors.toList()));
										}
									}
									return tile;
								}).collect(Collectors.toList());
								((AtoZWidget) widget).setTiles(tiles);
							}
						}
					}
				} catch (Exception e) {
				log.error("atoz lobby load [domainName="+domainName
						+", channel="+channel
						+", liveCasino="+liveCasino+"] " +
						e.getMessage(), e);
			}
		}
	}

	private void addTaggedGames(LobbyResponse config, String userGuid, Boolean liveCasino, String channel, Map<String, Set<String>> gameGuidProgressiveMap) throws UserNotFoundException,
			UserClientServiceFactoryException {
		if (!config.getPage().getWidgets().isEmpty()) {
			for (Widget widget : config.getPage().getWidgets()) {
				if (widget instanceof TileWidget && ((TileWidget) widget).getTileWidgetType() != null) {
					boolean taggedGamesWidget = ((TileWidget) widget).getTileWidgetType().equalsIgnoreCase("taggedGames");
					if (taggedGamesWidget) {
						lithium.service.user.client.objects.User user = userApiInternalClientService.getUserByGuid(userGuid);
						log.trace("Retrieved User from service-user #### " + user);
						if (user.getUserCategories() != null && !user.getUserCategories().isEmpty()) {
							List<String> tags = user.getUserCategories().stream()
									.map(UserCategory::getName)
									.collect(Collectors.toList());
							List<Tile> tagged = getTaggedGamesClient().get()
									.getTaggedGames(user.getDomain().getName(), tags, liveCasino, channel, true)
									.stream().map(game -> {
										Tile tile =  Tile.builder()
												.type("game")
												.gameId(game.getGameId())
												.gameName(game.getCommercialGameName())
												.supplierName(game.getSupplierName())
												.freeGame(game.getFreeGame())
												.gameStudioName(game.getGameStudioName())
												.image(game.getImage())
												.build();
										if (gameGuidProgressiveMap != null) {
											Set<String> progressiveJackpotIds = gameGuidProgressiveMap.get(game.getGameId());
											if (progressiveJackpotIds != null) {
												tile.setProgressiveIds(progressiveJackpotIds.stream().collect(Collectors.toList()));
											}
										}
										return tile;
									})
									.collect(Collectors.toList());
							((TileWidget) widget).setTiles(tagged);
						}
					}
				}
			}
		}
	}

	private void copy(LobbyRevision from, LobbyRevision to, String editorGuid) {
		to.setLobby(from.getLobby());
		to.setDescription(from.getDescription());
		to.setJson(from.getJson());
		to.setCreatedBy(userRepository.findOrCreateByGuid(editorGuid, () -> new User()));
		to = revisionRepository.save(to);
	}

	private Map<String, List<LobbyResponse>> mapLobbyConfigsByChannel(Lobby lobby)
			throws IOException, Status404LobbyConfigNotFoundException {
		Map<String, List<LobbyResponse>> lobbyConfigsByChannel = new LinkedHashMap<>();



		ObjectMapper om = new ObjectMapper();
		LobbyResponse[] configs = om.readValue(lobby.getCurrent().getJson(), LobbyResponse[].class);
		for (LobbyResponse config: configs) {
			if (config.getPage() == null || config.getPage().getChannel() == null) continue;
			if (lobbyConfigsByChannel.get(config.getPage().getChannel()) == null) {
				List<LobbyResponse> tempConfigs = new ArrayList<>();
				tempConfigs.add(config);
				lobbyConfigsByChannel.put(config.getPage().getChannel(), tempConfigs);
			} else {
				List<LobbyResponse> tempConfigs = lobbyConfigsByChannel.get(config.getPage().getChannel());
				tempConfigs.add(config);
				lobbyConfigsByChannel.replace(config.getPage().getChannel(), tempConfigs);
			}
		}

		return lobbyConfigsByChannel;
	}

	private Optional<RecentlyPlayedGamesClient> getRecentlyPlayedGamesClient() {
		return getClient(RecentlyPlayedGamesClient.class, "service-games");
	}

	private Optional<RecommendedGamesClient> getRecommendedGamesClient() {
		return getClient(RecommendedGamesClient.class, "service-games");
	}

	private Optional<TaggedGamesClient> getTaggedGamesClient() {
		return getClient(TaggedGamesClient.class, "service-games");
	}

	private Optional<GamesClient> getGamesClient() {
		return getClient(GamesClient.class, "service-games");
	}

	private Optional<ProgressiveJackpotFeedsClient> getProgressiveJackpotFeedsClient() {
		return getClient(ProgressiveJackpotFeedsClient.class, "service-games");
	}

	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;

		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}

		return Optional.ofNullable(clientInstance);
	}

	public lithium.service.user.client.objects.User getUserByGuid(String guid, List<lithium.service.user.client.objects.User> players) {
        Stream<lithium.service.user.client.objects.User> userStream = players.stream().filter(u -> u.getGuid().equals(guid));
        lithium.service.user.client.objects.User user = userStream.findFirst().get();
		return user;
	}

	public List<PageBanner> addPageBanners(List<PageBanner> pageBanners) {
		Iterable<PageBanner> resultsIterable = pageBannerRepository.saveAll(pageBanners);
		List<PageBanner> resultsList = new ArrayList<>();
		resultsIterable.forEach(resultsList::add);
		return resultsList;
	}

	public List<PageBanner> updatePageBannerPositions(Map<Long, Integer> idPositionMap) {
		List<PageBanner> pageBanners = pageBannerRepository.findAllByIdIn(idPositionMap.keySet().stream().toList());
		pageBanners.forEach(pageBanner -> pageBanner.setPosition(idPositionMap.get(pageBanner.getId())));
		Iterable<PageBanner> pageBannerIterable = pageBannerRepository.saveAll(pageBanners);
		List<PageBanner> pageBannersResult = new ArrayList<>();
		pageBannerIterable.forEach(pageBannersResult::add);
		return pageBannersResult;
	}

	public void removePageBanner(Long pageBannerId) {
		Optional<PageBanner> pageBannerOptional = pageBannerRepository.findById(pageBannerId);
		if(!pageBannerOptional.isPresent()) {
			throw new Status404BannerNotFound("Unable not delete, page banner not found: id:" + pageBannerId);
		}
		pageBannerOptional.get().setDeleted(true);
		pageBannerRepository.save(pageBannerOptional.get());
	}

	public PageBanner addPageBanner(String domainName, PageBanner pageBanner) {
		PageBanner oldPageBanner = pageBannerRepository.findByBannerDomainNameAndPrimaryNavCodeAndSecondaryNavCodeAndChannelAndLobbyIdAndBannerId(domainName,
				pageBanner.getPrimaryNavCode(), pageBanner.getSecondaryNavCode(), pageBanner.getChannel(), pageBanner.getLobby().getId(), pageBanner.getBanner().getId());
		if(oldPageBanner != null) {
			if(!oldPageBanner.getDeleted()) {
				throw new Status469InvalidInputException("Banner already exists on page");
			}
			pageBannerRepository.save(oldPageBanner);
			pageBanner.setId(oldPageBanner.getId());
		}
		pageBanner.setDeleted(false);
		return this.pageBannerRepository.save(pageBanner);
	}

	public List<PageBanner> retrievePageBanners(String domainName, String primaryNavCode, String secondaryNavCode, String channel) {
		return pageBannerRepository.findAllByDeletedFalseAndBannerDeletedFalseAndBannerDomainNameAndPrimaryNavCodeAndSecondaryNavCodeAndChannel(domainName, primaryNavCode, secondaryNavCode, channel);
	}

	public Optional<Lobby> findLobbyById(Long lobbyId){
		return repository.findById(lobbyId);
	}
}
