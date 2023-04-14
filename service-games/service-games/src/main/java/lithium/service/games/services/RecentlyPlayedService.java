package lithium.service.games.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.objects.RecentlyPlayedGameBasic;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameChannel;
import lithium.service.games.data.entities.GameGraphic;
import lithium.service.games.data.entities.RecentlyPlayed;
import lithium.service.games.data.entities.User;
import lithium.service.games.data.repositories.RecentlyPlayedRepository;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecentlyPlayedService {
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private GameService gameService;
	@Autowired private RecentlyPlayedRepository repository;
	@Autowired private UserService userService;

	private static final int RECENTLY_PLAYED_MAX_GAMES_FAIL_SAFE = 12;

	@Transactional
	public void add(Game game, Long userId) {
		// Hack to remove slotapi games from recently played games addition LSPLAT-833 PLAT-1546
		if (game.getGuid().contains("service-casino-provider-slotapi")) return;
		// FIXME: This is a hacky fix for https://jira.livescore.com/browse/PLAT-7883, recently played games needs
		//  	  further attention
		// Lock user, other threads wait.
		User user = userService.findForUpdate(userId);

		String gameGuid = game.getGuid().replaceAll(user.domainName() + "/", "");
		log.trace("RecentlyPlayedService.add | game: {}, user: {}, gameGuid: {}", game, user, gameGuid);

		if(game.isExcludeRecentlyPlayed()) return;

		Optional<RecentlyPlayed> rpOptional = retrieveByUserAndGame(user, game);
		log.trace("RecentlyPlayedService.add | after retrieveByUserAndGame: {}", rpOptional);
		if (rpOptional.isPresent()) {
			RecentlyPlayed rp = rpOptional.get();
			rp.setLastUsed(new Date());
			repository.save(rp);
		} else {
			repository.save(
				RecentlyPlayed.builder()
					.game(game)
					.user(user)
					.lastUsed(new Date())
					.build()
			);

			// Make a call to this to update the recently played list size for user
			getRecentlyPlayedGames(user.getGuid(), game.getLiveCasino(),  null);
		}
	}

	private Optional<RecentlyPlayed> retrieveByUserAndGame(User user, Game game) {
		return repository.findByUserAndGame(user, game);
	}

	public List<RecentlyPlayedGameBasic> getRecentlyPlayedGames(String userGuid, Boolean liveCasino, String channel) {
		User user = userService.findOrCreate(userGuid);
		return getRecentlyPlayedGames(user, liveCasino, channel).stream().map(rp -> {
			GameGraphic gameGraphic = null;

			try {
				gameGraphic = gameService.findCdnExternalGraphic(rp.getGame().getDomain().getName(),
						rp.getGame().getId(), liveCasino);
			} catch (Status500InternalServerErrorException e) {
				log.error("Problem trying to retrieve game graphic for recently played game [rp=" + rp + "] "
						+ e.getMessage(), e);
			}

			String supplierName = (rp.getGame().getGameSupplier() != null) ? rp.getGame().getGameSupplier().getName() : null;
			String gameStudioName = (rp.getGame().getGameStudio() != null) ? rp.getGame().getGameStudio().getName() : null;
			return RecentlyPlayedGameBasic.builder()
					.lastUsed(rp.getLastUsed())
					.gameId(rp.getGame().getGuid())
					.gameName(rp.getGame().getName())
					.commercialGameName(rp.getGame().getCommercialName())
					.supplierName(supplierName)
					.freeGame(rp.getGame().getFreeGame())
					.gameStudioName(gameStudioName)
					.image((gameGraphic != null) ? gameGraphic.getUrl() : null)
					.build();
		}).collect(Collectors.toList());
	}

	/**
	 * Gets a list of recently played games for a player
	 *
	 * @param user: the user for which the lobby load has been requested for
	 * @param liveCasino: true returns only live_casino, false retuns only casino
	 * @param channel: null = returns all channels OR only mobile_web, mobile_ios, desktop_web or android_native
	 * @return
	 */
	private List<RecentlyPlayed> getRecentlyPlayedGames(User user, Boolean liveCasino, String channel) {
		List<RecentlyPlayed> userRp = repository.findRecentlyPlayedByUserOrderByLastUsedAsc(user);
		log.trace("RecentlyPlayedService.getRecentlyPlayedGames | {}", userRp);
		if (liveCasino != null) {
			userRp = userRp.stream().filter(recentlyPlayed -> (recentlyPlayed.getGame().getLiveCasino().compareTo(liveCasino) == 0))
					.collect(Collectors.toList());
		}
		// Filters per channel specified or returns all channels when channel=null
		if (!StringUtil.isEmpty(channel) && !userRp.isEmpty()) {
			userRp = userRp.stream()
					.filter(recentlyPlayed -> {
						Optional<GameChannel> activeGameChannel = recentlyPlayed.getGame().getGameChannels().stream()
								.filter(activeChannel -> activeChannel.getChannel().getName().equals(channel))
								.findAny();
						boolean channelActive = activeGameChannel.isPresent();
						return channelActive;
					})
					.collect(Collectors.toList());
		}
		log.trace("RecentlyPlayedService.getRecentlyPlayedGames (after filter) | {}", userRp);

		int domainMaxRp = getDomainRecentlyPlayedGamesMax(user.domainName());
		if (userRp.size() > domainMaxRp) {
			List<RecentlyPlayed> userRpDelete = new ArrayList<>();
			int toRemove = userRp.size() - domainMaxRp;
			for (int i = 0; i < toRemove; i++) {
				userRpDelete.add(userRp.get(i));
				userRp.remove(i);
			}
			// FIXME: Product has been made aware and we will monitor;
			//  we could possibly have a cleanup job that checks that each channel has
			//  at least RECENTLY_PLAYED_GAMES_MAX per channel, and filter on endpoint to never
			//  display more than RECENTLY_PLAYED_GAMES_MAX on lobby
			// TODO Remove after fix LSPLAT-7776
			log.debug("RecentlyPlayedService.getRecentlyPlayedGames List to remove | {}", userRp);
			repository.deleteAll(userRpDelete);
		}

		Collections.reverse(userRp);
		log.trace("RecentlyPlayedService.getRecentlyPlayedGames (after delete) | {}", userRp);
		return userRp.stream()
				.filter(recentlyPlayed -> !recentlyPlayed.getGame().isExcludeRecentlyPlayed())
				.collect(Collectors.toList());
	}

	private RecentlyPlayed filterByGameGuid(String gameGuid, List<RecentlyPlayed> userRp) {
		return userRp.stream()
			.filter(rp -> {
				return rp.getGame().getGuid().contentEquals(gameGuid);
			})
			.findFirst()
			.orElse(null);
	}

	private int getDomainRecentlyPlayedGamesMax(String domainName) {
		try {
			String value = "";
			Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
			Optional<String> optionalValue = domain.findDomainSettingByName(
				DomainSettings.RECENTLY_PLAYED_GAMES_MAX.name());
			if (optionalValue.isPresent()) {
				value = optionalValue.get();
			} else {
				value = DomainSettings.RECENTLY_PLAYED_GAMES_MAX.defaultValue();
			}
			return Integer.parseInt(value);
		} catch (Status550ServiceDomainClientException | NumberFormatException e) {
			log.error("Failed to get domain recently played max games value for " + domainName
				+ ". Returning fail safe value of " + RECENTLY_PLAYED_MAX_GAMES_FAIL_SAFE + ". | " + e.getMessage());
			return RECENTLY_PLAYED_MAX_GAMES_FAIL_SAFE;
		}
	}
}
