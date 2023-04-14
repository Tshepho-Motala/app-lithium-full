package lithium.service.casino.provider.slotapi.services;

import lithium.exceptions.Status425DateParseException;
import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.slotapi.config.ProviderConfig;
import lithium.service.casino.provider.slotapi.config.ProviderConfigService;
import lithium.service.casino.provider.slotapi.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRepository;
import lithium.service.casino.provider.slotapi.storage.specifications.BetSpecifications;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.Game;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class BetHistoryService {
	@Autowired private BetRepository repository;
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private ProviderConfigService configService;
	@Autowired private LithiumServiceClientFactory lithiumServiceClientFactory;
	@Autowired private ModuleInfo moduleInfo;

	public Page<Bet> findBetHistory(String userGuid, Date start, Date end, String statuses, String games,
	        String betRoundGuid, boolean enrichRoundDetailUrl, String searchValue, Pageable pageable,
	        LithiumTokenUtil tokenUtil) throws Status425DateParseException {
		Specification<Bet> spec = null;

		String[] statusesArr = (statuses != null && !statuses.trim().isEmpty()) ? statuses.split(",") : null;
		String[] gamesArr = (games != null && !games.trim().isEmpty()) ? games.split(",") : null;

		spec = addToSpec(userGuid, spec, BetSpecifications::user);
		spec = addToSpec(betRoundGuid, spec, BetSpecifications::betRoundGuid);
		spec = addToSpec(start, false, spec, BetSpecifications::dateRangeStart);
		spec = addToSpec(end, true, spec, BetSpecifications::dateRangeEnd);
		spec = addToSpec(statusesArr, spec, BetSpecifications::statuses);
		spec = addToSpec(gamesArr, spec, BetSpecifications::games);
		spec = addToSpec(searchValue, spec, BetSpecifications::any);

		Page<Bet> result = repository.findAll(spec, pageable);

		if (!result.getContent().isEmpty()) {
			enrichTransactionGames(result);
			if (enrichRoundDetailUrl) {
				try {
					// TODO: If this ever becomes a global list, we need the domain name to be passed in from LBO.
					String domainName = userGuid.split("/")[0];
					enrichRoundDetailUrl(tokenUtil, domainName, result);
				} catch (Status500ProviderNotConfiguredException e) {
					log.error("Failed to enrich round detail url on bet history request for " + userGuid + " | " + e.getMessage());
				}
			}
		}

		return result;
	}

	public void enrichTransactionGames(Page<Bet> result) {
		Map<String, Game> domainGameMap = queryAllDomainGames();

		//loop through results and allocate
		result.getContent().stream()
			.filter(t -> t.getBetRound().getGame().getGuid() != null)
			.forEach(
				bet -> {
					String gameName = findGameName(
						bet.getBetRound().getUser().getDomain().getName(),
						bet.getBetRound().getGame().getGuid(),
						domainGameMap
					);

					bet.getBetRound().getGame().setGameName(gameName);
				}
			);
	}

	public void enrichRoundDetailUrl(LithiumTokenUtil tokenUtil, String domainName, Page<Bet> result)
			throws Status500ProviderNotConfiguredException {
		ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), domainName);
		if ((config.getBetHistoryRoundDetailUrl() != null && !config.getBetHistoryRoundDetailUrl().isEmpty()) &&
				(config.getBetHistoryRoundDetailPid() != null && !config.getBetHistoryRoundDetailPid().isEmpty())) {
			result.getContent().stream().forEach(t -> {
				StringBuilder url = new StringBuilder();
				url.append(config.getBetHistoryRoundDetailUrl());
				url.append("&provider=" + config.getBetHistoryRoundDetailPid());
				url.append("&roundID=" + t.getBetRound().getGuid());
				url.append("&token=" + tokenUtil.getTokenValue());
				t.getBetRound().setRoundDetailUrl(url.toString());
			});
		}
	}

	public String findGameName(String domainName, String gameGuid, Map<String, Game> domainGameMap) {
		String domainGameKey = (gameGuid!=null&&(gameGuid.startsWith(domainName+"/")))?gameGuid:domainName + "/service-casino-provider-slotapi_" + gameGuid;

		if (domainGameMap.containsKey(domainGameKey)) {
			return domainGameMap.get(domainGameKey).getName();
		}

		return null;
	}

	public Map<String, Game> queryAllDomainGames() {
		HashMap<String, Game> domainGameMap = new HashMap<>();
		try {
			//Retrieve domain list
			DomainClient domainClient = cachingDomainClientService.getDomainClient();
			Iterable<Domain> allDomains = domainClient.findAllDomains().getData();

			//Retrieve game list per domain
			for (Domain domain : allDomains) {
				GamesClient gamesClient = lithiumServiceClientFactory.target(GamesClient.class, "service-games", true);
				gamesClient.listDomainGames(domain.getName()).getData().forEach(game ->
						domainGameMap.put(domain.getName() + "/" + game.getGuid(), game)
				);
			}
		} catch (Exception exception) {
			log.error("Unable to build domain game list", exception);
		}

		return domainGameMap;
	}

	private Specification<Bet> addToSpec(final String aString, Specification<Bet> spec, Function<String,
			Specification<Bet>> predicateMethod) {
		if (aString != null && !aString.isEmpty()) {
			Specification<Bet> localSpec = Specification.where(predicateMethod.apply(aString));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<Bet> addToSpec(final Date aDate, boolean addDay, Specification<Bet> spec,
	        Function<Date, Specification<Bet>> predicateMethod) {
		if (aDate != null) {
			DateTime someDate = new DateTime(aDate);
			if (addDay) {
				someDate = someDate.plusDays(1).withTimeAtStartOfDay();
			} else {
				someDate = someDate.withTimeAtStartOfDay();
			}
			Specification<Bet> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<Bet> addToSpec(final String[] sArray, Specification<Bet> spec,
	        Function<String[], Specification<Bet>> predicateMethod) {
		if (sArray != null && sArray.length > 0) {
			Specification<Bet> localSpec = Specification.where(predicateMethod.apply(sArray));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}
}
