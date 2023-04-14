package lithium.service.games.services;

import lithium.service.casino.ProgressiveJackpotFeedsClientService;
import lithium.service.casino.client.objects.Game;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotFeedResponse;
import lithium.service.games.data.entities.GameSupplier;
import lithium.service.games.data.entities.progressivejackpotfeeds.Currency;
import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotBalance;
import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotFeed;
import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotGameBalance;
import lithium.service.games.data.entities.progressivejackpotfeeds.projection.entities.GameProjection;
import lithium.service.games.data.repositories.CurrencyRepository;
import lithium.service.games.data.repositories.ProgressiveJackpotBalanceRepository;
import lithium.service.games.data.repositories.ProgressiveJackpotGameBalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProgressiveJackpotFeedsBalanceSyncService {
    @Autowired private ProgressiveJackpotFeedsBalanceSyncService self;
    @Autowired private CurrencyRepository currencyRepo;
    @Autowired private GameService gameService;
    @Autowired private GameSupplierService gameSupplierService;
    @Autowired private ModelMapper modelMapper;
    @Autowired private ProgressiveJackpotBalanceRepository balanceRepo;
    @Autowired private ProgressiveJackpotFeedsClientService client;
    @Autowired private ProgressiveJackpotGameBalanceRepository gameBalanceRepo;
    @Autowired private ProgressiveJackpotFeedService feedService;

    public void sync() {
        List<ProgressiveJackpotFeed> feeds = feedService.getAllEnabledProgressiveJackpotFeeds();
        log.trace("Syncing progressive jackpot feeds | feeds: {}", feeds);

        if (!feeds.isEmpty()) {
            List<ProgressiveJackpotFeedResponse> responses = invokeProgressiveJackpotFeedLookups(feeds);

            Map<String, ProgressiveJackpotBalance> balanceMap = new LinkedHashMap<>();
            List<ProgressiveJackpotGameBalance> gameBalanceList = new ArrayList<>();

            responses.stream().forEach(response -> {
                if (response != null && !response.getProgressiveJackpotGameBalances().isEmpty()) {
                    response.getProgressiveJackpotGameBalances().stream().forEach(gameBalance -> {

                        Currency currency = currencyRepo.findOrCreateByCode(gameBalance.getCurrencyCode(),
                                Currency::new);
                        String domainName = gameBalance.getGame().getGameSupplier().getDomain().getName();
                        String gameSupplierName = gameBalance.getGame().getGameSupplier().getName();
                        String gameGuid = gameBalance.getGame().getGuid();

                        addToBalanceMap(balanceMap, gameBalance.getProgressiveId(), gameBalance.getAmount(),
                                gameBalance.getWonByAmount(), currency, domainName, gameSupplierName);
                        addToGameBalanceList(gameBalanceList, gameBalance.getProgressiveId(), gameBalance.getAmount(),
                                gameBalance.getWonByAmount(), currency, domainName, gameGuid);
                    });
                }
            });

            persist(balanceMap, gameBalanceList);
        }
    }

    private String computeProgressiveId(Long gameSupplierId, String progressiveId) {
        return gameSupplierId + "-" + progressiveId;
    }

    private void addToBalanceMap(Map<String, ProgressiveJackpotBalance> map, String progressiveId, BigDecimal amount,
            BigDecimal wonByAmount, Currency currency, String domainName, String gameSupplierName) {
        GameSupplier gameSupplier = gameSupplierService.findByDomainAndSupplierName(domainName, gameSupplierName);

        String computedProgressiveId = computeProgressiveId(gameSupplier.getId(), progressiveId);

        map.putIfAbsent(computedProgressiveId, ProgressiveJackpotBalance.builder()
                .progressiveId(computedProgressiveId)
                .amount(amount)
                .wonByAmount(wonByAmount)
                .currency(currency)
                .gameSupplier(gameSupplier)
                .build());
    }

    private void addToGameBalanceList(List<ProgressiveJackpotGameBalance> gameBalanceList, String progressiveId,
             BigDecimal amount, BigDecimal wonByAmount, Currency currency, String domainName, String gameGuid) {
        lithium.service.games.data.entities.Game game = gameService
                .findByGameAndDomainName(gameGuid, domainName);

        String computedProgressiveId = computeProgressiveId(game.getGameSupplier().getId(), progressiveId);

        gameBalanceList.add(ProgressiveJackpotGameBalance.builder()
                .progressiveId(computedProgressiveId)
                .amount(amount)
                .wonByAmount(wonByAmount)
                .currency(currency)
                .game(game)
                .build());
    }

    private void persist(Map<String, ProgressiveJackpotBalance> balanceMap,
            List<ProgressiveJackpotGameBalance> gameBalanceList) {
        List<ProgressiveJackpotBalance> balanceList = balanceMap.values().stream().toList();

        // Remove all entries from DB
        balanceRepo.deleteAll();
        gameBalanceRepo.deleteAll();

        // Persist refreshed data to DB
        balanceList = balanceRepo.saveAll(balanceList);
        gameBalanceList = gameBalanceRepo.saveAll(gameBalanceList);

        // Persist refreshed data to caches
        balanceList.stream()
            .map(balance -> modelMapper.map(balance,
                    lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotBalance.class))
            .collect(Collectors.groupingBy(balance -> balance.getGameSupplier().getDomain().getName()))
            .forEach((domainName, balances) -> self.cachePutBalancesByDomain(domainName, balances));

        gameBalanceList.stream()
            .map(balance -> modelMapper.map(balance,
                    lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance.class))
            .collect(Collectors.groupingBy(balance -> balance.getGame().getGameSupplier().getDomain().getName()))
            .forEach((domainName, balances) -> self.cachePutGameBalancesByDomain(domainName, balances));

    }

    private List<ProgressiveJackpotFeedResponse> invokeProgressiveJackpotFeedLookups(
            List<ProgressiveJackpotFeed> feeds) {
        ExecutorService executor = Executors.newFixedThreadPool(feeds.size());
        List<Callable<ProgressiveJackpotFeedResponse>> callables = createCallables(feeds);

        List<ProgressiveJackpotFeedResponse> responses = new ArrayList<>();

        List<Future<ProgressiveJackpotFeedResponse>> futures = new ArrayList<>();

        callables.forEach(callable ->
                futures.add(executor.submit(callable))
        );
        for (Future<ProgressiveJackpotFeedResponse> future : futures) {
            try {
                responses.add(future.get(5, TimeUnit.SECONDS));
                future.cancel(true);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("Failed to get results", e);
                future.cancel(true);
            }
        }
        executor.shutdown();

        return responses;
    }

    private List<Callable<ProgressiveJackpotFeedResponse>> createCallables(List<ProgressiveJackpotFeed> list) {
        List<Callable<ProgressiveJackpotFeedResponse>> callables = list.stream().map(feed -> {
            try {
                return (Callable<ProgressiveJackpotFeedResponse>) () -> {
                    try {
                        ProgressiveJackpotFeedResponse response = getProgressiveJackpotFeedByProvider(feed);
                        log.trace("Response for {} | {}", feed.getGameSupplier().getName(), response);
                        return response;
                    } catch (Exception e) {
                        log.warn("Failed to get jackpot feeds for service. [Feed: " + feed + "] " + e.getMessage());
                        return null;
                    }
                };

            } catch (Exception e) {
                log.warn("Failed to create callable. [Feed: " + feed + "] " + e.getMessage());
                return (Callable<ProgressiveJackpotFeedResponse>) () -> null;
            }
        }).collect(Collectors.toList());
        return callables.stream().filter(callable -> callable != null).collect(Collectors.toList());
    }

    private ProgressiveJackpotFeedResponse getProgressiveJackpotFeedByProvider(ProgressiveJackpotFeed feed) {
        log.trace("getProgressiveJackpotFeedByProvider | feed: {}", feed);
        List<GameProjection> enabledGames = gameService
                .getAllEnabledProgressiveJackpotFeedsGamesBySupplier(feed.getGameSupplier(),
                        true, true);
        if (!enabledGames.isEmpty()) {
            List<lithium.service.casino.client.objects.Game> games = modelMapper.map(
                    enabledGames,
                    new TypeToken<List<lithium.service.casino.client.objects.Game>>() {}.getType());
            String domainName = feed.getGameSupplier().getDomain().getName();
            String moduleName = feed.getModule().getName();
            String gameSupplierName = feed.getGameSupplier().getName();
            return client.getProgressiveJackpotFeed(domainName, moduleName,
                    gameSupplierName, games);
        }
        log.warn("getProgressiveJackpotFeedByProvider | There are no enabled progressive jackpot feed games for {}",
                feed.getGameSupplier().getName());
        return null;
    }

    @CachePut(cacheNames = "lithium.service.games.progressive-jackpots.balances-by-domain", key = "{#root.args[0]}")
    public List<lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotBalance> cachePutBalancesByDomain(
            String domain,
            List<lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotBalance> progressiveBalanceData) {
        log.trace("cachePutBalanceByDomain | domain: {}, progressiveBalanceData: {}", domain, progressiveBalanceData);
        return progressiveBalanceData;
    }

    @CachePut(cacheNames = "lithium.service.games.progressive-jackpots.game-balances-by-domain", key = "{#root.args[0]}")
    public List<lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance> cachePutGameBalancesByDomain(
            String domain,
            List<lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance> progressiveBalanceData) {
        log.trace("cachePutGameBalanceByDomain | domain: {}, progressiveBalanceData: {}", domain, progressiveBalanceData);
        return progressiveBalanceData;
    }
}
