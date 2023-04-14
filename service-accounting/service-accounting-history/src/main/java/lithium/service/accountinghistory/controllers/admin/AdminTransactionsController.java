package lithium.service.accountinghistory.controllers.admin;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status425DateParseException;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.accountinghistory.service.AccountingService;
import lithium.service.accountinghistory.service.RewardsService;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.objects.TransactionDetailPayload;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.Game;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin/transactions")
public class AdminTransactionsController {
    @Autowired AccountingService accountingService;
    @Autowired
    RewardsService rewardsService;
    @Autowired CasinoClientService casinoClientService;
    @Autowired CachingDomainClientService cachingDomainClientService;
    @Autowired LithiumServiceClientFactory services;

    @PostMapping(value = "/table")
    public DataTableResponse<TransactionEntryBO> table(
            @RequestParam(name = "dateRangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeStart,
            @RequestParam(name = "dateRangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeEnd,
            @RequestParam(name = "userGuid", required = false) String userGuid,
            @RequestParam(name = "transactionId", required = false) String transactionId,
            @RequestParam(name = "draw") String draw,
            @RequestParam(name = "start") int start,
            @RequestParam(name = "length") int length,
            @RequestParam(name = "search", required = false) String searchValue,
            @RequestParam(name = "providerTransId", required = false) String providerTransId,
            @RequestParam(name = "providerGuid", required = false) String providerGuid,
            @RequestParam(name = "transactionType", required = false) List<String> transactionType,
            @RequestParam(name = "additionalTransId", required = false) String additionalTransId,
            @RequestParam(name = "order[0][dir]", required = false) String orderDirection,
            @RequestParam(name = "domainName", required = false) String domainName,
            @RequestParam(name = "accountCode", required = false) String accountCode,
            @RequestParam(name = "roundId", required = false) String roundId,
            LithiumTokenUtil tokenUtil
    ) throws Status425DateParseException, Status510AccountingProviderUnavailableException {
        log.debug("Transactions table requested [dateRangeStart=" + dateRangeStart
                + ", dateRangeEnd=" + dateRangeStart + ", userGuid=" + userGuid + ", transactionId=" + transactionId
                + ", roundId=" + roundId + ", draw=" + draw + ", start=" + start + ", length=" + length + ", search=" + searchValue + "]");

        if (length > 100) length = 100;

        DataTableResponse<TransactionEntryBO> result = accountingService.adminTransactionsClient().table(dateRangeStart, dateRangeEnd, userGuid,
                transactionId, draw, start, length, searchValue, providerGuid, providerTransId, transactionType, additionalTransId,
                orderDirection, tokenUtil.getAccessToken().getValue(), domainName, accountCode, roundId);

        enrichTransactionExternalURL(result);

        Map<String, Game> domainGameMap = queryAllDomainGames();
        enrichTransactionGames(domainGameMap, result);
        rewardsService.enrichRewardInformation(domainGameMap, result);

        return result;
    }

    private void enrichTransactionGames(Map<String, Game> domainGameMap, DataTableResponse<TransactionEntryBO> result) {
        //loop through results and allocate
        result.getData().stream()
                .filter(t -> t.getDetails().getGameGuid() != null)
                .forEach(
                        transactionEntryBO -> {
                            String gameName = findGameName(
                                    transactionEntryBO.getAccount().getDomain().getName(),
                                    transactionEntryBO.getDetails().getGameGuid(),
                                    domainGameMap
                            );

                            transactionEntryBO.getDetails().setGameName(gameName);
                        }
                );
    }

    private void enrichTransactionExternalURL(DataTableResponse<TransactionEntryBO> result) {
        List<TransactionDetailPayload> queryData = result.getData().stream()
                .filter(c -> c.getDetails() != null && c.getDetails().getProviderGuid() != null)
                .map(c -> TransactionDetailPayload.builder()
                        .providerGuid(
                                c.getDetails().getProviderGuid().contains("/")
                                        ? c.getDetails().getProviderGuid()
                                        : c.getAccount().getDomain().getName() + "/" + c.getDetails().getProviderGuid()
                        )
                        .transactionType(c.getTransaction().getTransactionType().getCode())
                        .providerTransactionGuid(c.getDetails().getExternalTranId()) // todo provider tran id - to confirm
                        .build())
                .collect(Collectors.toList());

        log.debug("AdminTransactionsController query: " + queryData);

        try {
            final List<TransactionDetailPayload> detailResponseList = casinoClientService.findTransactionDetailUrls(queryData);

            detailResponseList.forEach(p -> {
                log.debug("AdminTransactionsController->table: " + p.toString());
            });

            detailResponseList.forEach(detailResponse -> {
                result.getData()
                        .stream()
                        .filter(playerTran -> playerTran.getDetails().getExternalTranId() != null)
                        .filter(playerTran -> playerTran.getDetails().getExternalTranId().contentEquals(detailResponse.getProviderTransactionGuid()))
                        .filter(playerTran -> playerTran.getDetails().getExternalTransactionDetailUrl() == null)
                        .filter(playerTran -> playerTran.getTransaction().getTransactionType().getCode().equalsIgnoreCase(detailResponse.getTransactionType()))
                        .findFirst()
                        .ifPresent(matchedTran -> {
                            matchedTran.getDetails().setExternalTransactionDetailUrl(detailResponse.getTransactionDetailUrl());
                        });
            });

        } catch (ErrorCodeException e) {
            log.debug("Problem looking up transaction details: " + e.getMessage(), e);
        } catch (Exception e) {
            log.info("Problem looking up transaction details: " + e.getMessage(), e);
        }
    }

    private Map<String, Game> queryAllDomainGames() {
        HashMap<String, Game> domainGameMap = new HashMap<>();
        try {
            //retrieve domain list
            DomainClient domainClient = cachingDomainClientService.getDomainClient();
            Iterable<Domain> allDomains = domainClient.findAllDomains().getData();

            //retrieve game list per domain
            for (Domain domain : allDomains) {
                GamesClient gamesClient = services.target(GamesClient.class, "service-games", true);
                gamesClient.listDomainGames(domain.getName()).getData().forEach(game ->
                        domainGameMap.put(domain.getName() + "/" + game.getGuid(), game)
                );
            }
        } catch (Exception e) {
            log.error("Unable to build domain game list", e);
        }
        return domainGameMap;
    }

    private String findGameName(String domainName, String gameGuid, Map<String, Game> domainGameMap) {
        String domainGameKey = (gameGuid!=null&&(gameGuid.startsWith(domainName+"/")))?gameGuid:domainName + "/" + gameGuid;

        if (domainGameMap.containsKey(domainGameKey)) {
            return domainGameMap.get(domainGameKey).getName();
        }

        return null;
    }
}

