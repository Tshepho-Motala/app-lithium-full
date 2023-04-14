package lithium.service.casino.search.services.casino;

import static java.util.Optional.ofNullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lithium.exceptions.Status425DateParseException;
import lithium.service.casino.client.objects.CasinoBetHistoryCsv;
import lithium.service.casino.client.objects.request.CommandParams;
import lithium.service.casino.client.objects.response.CasinoBetHistoryCsvResponse;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.search.data.repositories.casino.BetRepository;
import lithium.service.casino.search.data.repositories.casino.BetRoundRepository;
import lithium.service.casino.search.data.specifications.BetRoundSpecifications;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.Game;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "casino.BetHistoryService")
public class BetHistoryService {
  @Autowired
  private CachingDomainClientService cachingDomainClientService;

  @Autowired @Qualifier("casino.BetRepository")
  private BetRepository betRepository;

  @Autowired @Qualifier("casino.BetRoundRepository")
  private BetRoundRepository repository;

  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private LithiumServiceClientFactory services;

  private static final String PROVIDER_PROPERTY_BET_HISTORY_ROUND_DETAIL_URL = "betHistoryRoundDetailUrl";
  private static final String PROVIDER_PROPERTY_BET_HISTORY_ROUND_DETAIL_PID = "betHistoryRoundDetailPid";

  public Page<BetRound> findBetHistory(String userGuid, Date start, Date end, String providers, String statuses, String games,
      String betRoundGuid, boolean enrichRoundDetailUrl, String searchValue, Pageable pageable,
      LithiumTokenUtil tokenUtil) throws Status425DateParseException {
    // TODO: If this ever becomes a global list, we need the domain name to be passed in from LBO.
    String domainName = userGuid.split("/")[0];

    Specification<BetRound> spec = null;

    String[] statusesArr = (statuses != null && !statuses.trim().isEmpty()) ? statuses.split(",") : null;
    String[] gamesArr = (games != null && !games.trim().isEmpty()) ? games.split(",") : null;
    if (gamesArr != null && gamesArr.length > 0) {
      for (int i = 0; i < gamesArr.length; i++) {
        String gameGuid = domainName + "/" + gamesArr[i];
        gamesArr[i] = gameGuid;
      }
    }
    String[] providerArr = (providers != null && !providers.trim().isEmpty()) ? providers.split(",") : null;

    spec = addToSpec(userGuid, spec, BetRoundSpecifications::user);
    spec = addToSpec(betRoundGuid, spec, BetRoundSpecifications::betRoundGuid);
    spec = addToSpec(start, false, spec, BetRoundSpecifications::dateRangeStart);
    spec = addToSpec(end, true, spec, BetRoundSpecifications::dateRangeEnd);
    spec = addToSpec(statusesArr, spec, BetRoundSpecifications::statuses);
    spec = addToSpec(gamesArr, spec, BetRoundSpecifications::games);
    spec = addToSpec(providerArr, spec, BetRoundSpecifications::providers);
    spec = addToSpec(searchValue, spec, BetRoundSpecifications::any);

    Page<BetRound> result = repository.findAll(spec, pageable);

    if (!result.getContent().isEmpty()) {
      enrichBetAmount(result);
      enrichTransactionGames(result, domainName);
      if (enrichRoundDetailUrl) {
        enrichRoundDetailUrl(tokenUtil, domainName, result);
      }
    }

    return result;
  }

  private void enrichBetAmount(Page<BetRound> result) {
    for (BetRound betRound: result.getContent()) {
      double betAmount = betRepository.getBetAmount(betRound.getId(), betRound.getProvider().getId());
      double betReversalAmount = betRepository.getBetReversalAmount(betRound.getId(), betRound.getProvider().getId());
      betRound.setBetAmount(betAmount - betReversalAmount);
    }
  }

  private void enrichRoundDetailUrl(LithiumTokenUtil tokenUtil, String domainName, Page<BetRound> result) {
    Map<String, Provider> providers = new LinkedHashMap<>();
    for (BetRound betRound: result.getContent()) {
      String providerGuid = betRound.getProvider().getGuid();
      Provider provider = null;
      if (providers.get(providerGuid) != null) {
        provider = providers.get(providerGuid);
      }
      if (provider == null) {
        String providerUrl = betRound.getProvider().getGuid().split("/")[1];
        provider = getProviderClient().get().findByUrlAndDomainName(providerUrl, domainName).getData();
        providers.put(providerGuid, provider);
      }
      if (provider != null) {
        String url = provider.getPropertyValue(PROVIDER_PROPERTY_BET_HISTORY_ROUND_DETAIL_URL);
        String pid = provider.getPropertyValue(PROVIDER_PROPERTY_BET_HISTORY_ROUND_DETAIL_PID);

        if (url != null && !url.trim().isEmpty() && pid != null && !pid.trim().isEmpty()) {
          StringBuilder sb = new StringBuilder();
          sb.append(url);
          sb.append("&provider=" + pid);
          sb.append("&roundID=" + betRound.getGuid());
          sb.append("&token=" + tokenUtil.getTokenValue());
          betRound.setRoundDetailUrl(sb.toString());
        }
      }
    }
  }

  private void enrichTransactionGames(Page<BetRound> result, String domainName) {
    Map<String, Game> domainGameMap = queryAllDomainGames(domainName);

    //loop through results and allocate
    result.getContent().stream()
        .filter(betRound -> betRound.getGame().getGuid() != null)
        .forEach(betRound -> enrichGameData(betRound, domainGameMap));
  }

  private Map<String, Game> queryAllDomainGames(String domainName) {
    HashMap<String, Game> domainGameMap = new HashMap<>();
    try {
      getGamesClient().get().listDomainGames(domainName).getData().forEach(game -> {
        domainGameMap.put(domainName + "/" + game.getGuid(), game);
      });
    } catch (Exception exception) {
      log.error("Unable to build domain game list", exception);
    }

    return domainGameMap;
  }

  private void enrichGameData(BetRound betRound, Map<String, Game> domainGameMap) {
    String gameGuid = betRound.getGame().getGuid();
    String domainName = betRound.getUser().getDomain().getName();
    String provider = betRound.getProvider().getGuid();
    String domainGameKey = (gameGuid!=null&&(gameGuid.startsWith(domainName+"/")))?gameGuid:domainName + "/" + provider + "_" + gameGuid;

    if (domainGameMap.containsKey(domainGameKey)) {
      Game game = domainGameMap.get(domainGameKey);
      betRound.getGame().setName(game.getCommercialName());
      betRound.getGame().setProviderGuid(game.getProviderGuid());
      betRound.getGame().setCategory("");
      if (game.getGameSupplier() != null) {
        betRound.getGame().setSupplier(game.getGameSupplier().getName());
      }
    }
  }

  private Specification<BetRound> addToSpec(final String aString, Specification<BetRound> spec, Function<String,
      Specification<BetRound>> predicateMethod) {
    if (aString != null && !aString.isEmpty()) {
      Specification<BetRound> localSpec = Specification.where(predicateMethod.apply(aString));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<BetRound> addToSpec(final Date aDate, boolean addDay, Specification<BetRound> spec,
      Function<Date, Specification<BetRound>> predicateMethod) {
    if (aDate != null) {
      DateTime someDate = new DateTime(aDate);
      if (addDay) {
        someDate = someDate.plusDays(1).withTimeAtStartOfDay();
      } else {
        someDate = someDate.withTimeAtStartOfDay();
      }
      Specification<BetRound> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<BetRound> addToSpec(final String[] sArray, Specification<BetRound> spec,
      Function<String[], Specification<BetRound>> predicateMethod) {
    if (sArray != null && sArray.length > 0) {
      Specification<BetRound> localSpec = Specification.where(predicateMethod.apply(sArray));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Optional<ProviderClient> getProviderClient() {
    return getClient(ProviderClient.class, "service-domain");
  }

  private Optional<GamesClient> getGamesClient() {
    return getClient(GamesClient.class, "service-games");
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

  public CasinoBetHistoryCsvResponse generateCsvRecords(CommandParams commandParams, LithiumTokenUtil tokenUtil) {

    Map<String, String> params = commandParams.getParamsMap();
    String userGuid = params.get("userGuid");
    Long dateRangeStart = ofNullable(params.get("dateRangeStart")).map(Long :: valueOf).orElse(null);
    Long dateRangeEnd = ofNullable(params.get("dateRangeEnd")).map(Long :: valueOf).orElse(null);
    String providers = params.get("providers");
    String statuses = params.get("statuses");
    String games = params.get("games");
    String betRoundGuid = params.get("betRoundGuid");

    int page = Optional.ofNullable(params.get("page")).map(Integer :: valueOf ).orElse(0);
    int size = Optional.ofNullable(params.get("size")).map(Integer :: valueOf ).orElse(10000);;

    Date createdStartDate = null;
    if ((dateRangeStart != null) && (dateRangeStart != -1)) {
      createdStartDate = new Date(dateRangeStart);
    }
    Date createdEndDate = null;
    if ((dateRangeEnd != null) && (dateRangeEnd != -1)) {
      createdEndDate = new Date(dateRangeEnd);
    }

    DataTableRequest request = new DataTableRequest();
    PageRequest pageRequest = PageRequest.of(page, size);
    request.setPageRequest(pageRequest);
    Page<BetRound> betRoundPage = findBetHistory(userGuid, createdStartDate, createdEndDate, providers, statuses, games, betRoundGuid, true,
        request.getSearchValue(), request.getPageRequest(), tokenUtil);

    List<CasinoBetHistoryCsv> casinoBetHistoryCsvList = betRoundPage.getContent().stream().map(betRound -> {
      try {
        return buildCasinoHistoryCsv(betRound);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toList());

    return CasinoBetHistoryCsvResponse.builder()
        .totalElements(betRoundPage.getTotalElements())
        .totalPages(betRoundPage.getTotalPages())
        .casinoBetHistoryCsvList(casinoBetHistoryCsvList)
        .build();

  }

  private CasinoBetHistoryCsv buildCasinoHistoryCsv (BetRound betRound) {
    return CasinoBetHistoryCsv.builder()
        .createdDate(DATE_FORMAT.format(betRound.getCreatedDate()))
        .gameName(betRound.getGame().getName())
        .gameSupplier(betRound.getGame().getSupplier())
        .complete((betRound.getLastBetResult() != null ? betRound.getLastBetResult().getBetResultKind().getCode() : "OPEN"))
        .betAmount(betRound.getBetAmount()+"")
        .roundReturnsTotal(betRound.getRoundReturnsTotal()+"")
        .betRoundStatus(betRound.getLastBetResult() != null ? betRound.getLastBetResult().isRoundComplete()  ? "COMPLETE" : "NOT COMPLETE" : "NOT COMPLETE")
        .betSettledDate(betRound.getLastBetResult() != null ? DATE_FORMAT.format(betRound.getLastBetResult().getTransactionTimestamp()) : null)
        .betRoundGuid(betRound.getGuid())
        .providerName(betRound.getProvider().getGuid())
        .build();
  }
}
