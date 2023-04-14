package lithium.service.casino.provider.roxor.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.leader.LeaderCandidate;
import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.config.EvolutionDirectGameLaunchApiProviderConfig;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.data.response.evolution.BetLimits;
import lithium.service.casino.provider.roxor.data.response.evolution.Dealer;
import lithium.service.casino.provider.roxor.data.response.evolution.GameTable;
import lithium.service.casino.provider.roxor.data.response.evolution.OperationHours;
import lithium.service.casino.provider.roxor.data.response.evolution.Road;
import lithium.service.casino.provider.roxor.data.response.evolution.State;
import lithium.service.casino.provider.roxor.data.response.evolution.VideoSnapshot;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.objects.supplier.SupplierGameMetaBetLimit;
import lithium.service.games.client.objects.supplier.SupplierGameMetaData;
import lithium.service.games.client.objects.supplier.SupplierGameMetaDataMessage;
import lithium.service.games.client.objects.supplier.SupplierGameMetaDealer;
import lithium.service.games.client.objects.supplier.SupplierGameMetaDescription;
import lithium.service.games.client.objects.supplier.SupplierGameMetaHours;
import lithium.service.games.client.objects.supplier.SupplierGameMetaLinks;
import lithium.service.games.client.objects.supplier.SupplierGameMetaResults;
import lithium.service.games.client.objects.supplier.SupplierGameMetaVertical;
import lithium.service.games.client.stream.SupplierGameMetaDataStream;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EvolutionGamesMetaDataScheduledService {

    private EvolutionClientService evolutionClientService;

    private LeaderCandidate leaderCandidate;

    @Autowired
    private CachingDomainClientService cachingDomainClientService;

    @Autowired
    ProviderConfigService providerConfigService;

    @Autowired
    ModuleInfo moduleInfo;

    @Autowired
    private SupplierGameMetaDataStream supplierGameMetaDataStream;

    @Autowired
    public EvolutionGamesMetaDataScheduledService(EvolutionClientService evolutionClientService, LeaderCandidate leaderCandidate) {
        this.evolutionClientService = evolutionClientService;
        this.leaderCandidate = leaderCandidate;
    }

    @Scheduled(fixedDelayString = "${lithium.services.casino.provider.roxor.evolution.direct-game-launch-scheduler-in-milliseconds:20000}")
    public void evolutionGamesMetaDataScheduler() {
        log.debug("EvolutionGamesMetaDataScheduledService running");
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }

        Map<String, String> domainDataMap = new HashMap<>();

        Response<List<Domain>> playerDomains = cachingDomainClientService.getDomainClient().findAllPlayerDomains();
        if (playerDomains.isSuccessful() && playerDomains.getData() != null) {
            playerDomains.getData().forEach(playerDomain -> {
                EvolutionDirectGameLaunchApiProviderConfig config = null;
                try {
                    config = providerConfigService.getEvolutionDirectGameLaunchApiProviderConfigs(moduleInfo.getModuleName(), playerDomain.getName());
                } catch (Status512ProviderNotConfiguredException e) {
                    log.debug("Provider not configured for domain: " + playerDomain.getName());
                    return;
                } catch (Exception e) {
                    log.debug("Failed to get provider configs for domain: " + playerDomain.getName(), e);
                    return;
                }
                log.debug("Getting evolution game data for domain: " + playerDomain.getName() + " Config: " + config);

                if (!Objects.isNull(config)) {
                    String key = (config.getUrl() + config.getCasinoId()).toLowerCase();
                    String stateString = domainDataMap.get(key);

                    if (Objects.isNull(stateString)) {
                        stateString = evolutionClientService.getLobbyStateByDomain(config);
                        if(!Objects.isNull(stateString)) {
                            domainDataMap.put(key, stateString);
                        }
                    }

                    if (Objects.isNull(stateString))
                        return;

                    try {
                        SupplierGameMetaDataMessage supplierGameMetaDataMessage = buildSupplierGameMetaDataMessage(stateString, playerDomain.getName());
                        supplierGameMetaDataStream.process(supplierGameMetaDataMessage);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to process json for domain: " + playerDomain.getName() + " config: " + config);
                    }
                } else {
                    log.debug("Missing evolution config properties for domain: " + playerDomain.getName());
                }
            });
        }
    }

    public SupplierGameMetaDataMessage buildSupplierGameMetaDataMessage(String stateString, String domainName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        State state = mapper.readValue(stateString, State.class);
        List<SupplierGameMetaData> supplierGameMetaDataList = new ArrayList<>();

        state.getTables().keySet().forEach(tableId -> {
            GameTable gameTable = state.getTables().get(tableId);
            SupplierGameMetaVertical gameVertical = SupplierGameMetaVertical.builder()
                    .name(gameTable.getGameVertical())
                    .build();

            List<SupplierGameMetaBetLimit> betLimitsList = buildSupplierGameMetaBetLimitsList(gameTable.getBetLimits());

            Set<SupplierGameMetaDescription> descriptions = buildSupplierGameMetaDescription(gameTable.getDescriptions());

            SupplierGameMetaHours supplierGameMetaHours = buildSupplierGameMetaHours(gameTable.getOperationHours());

            List<SupplierGameMetaLinks> links = buildSupplierGameMetaLinksList(gameTable.getVideoSnapshot());

            SupplierGameMetaDealer supplierGameMetaDealer = buildSupplierGameMetaDealer(gameTable.getDealer());

            List<SupplierGameMetaResults> supplierGameMetaResultsList = buildSupplierGameMetaResults(gameTable.getRoad());

            if (supplierGameMetaResultsList == null || supplierGameMetaResultsList.isEmpty()) {
                supplierGameMetaResultsList = buildSupplierGameMetaResults(stateString, tableId);
            }

            SupplierGameMetaData supplierGameMetaData = SupplierGameMetaData.builder()
                    .supplierGameGuid(tableId)
                    .gameType(gameTable.getGameType())
                    .name(gameTable.getName())
                    .open(gameTable.getOpen())
                    .gameSubType(gameTable.getGameSubType())
                    .gameVertical(gameVertical)
                    .betLimits(betLimitsList)
                    .players(gameTable.getPlayers())
                    .descriptions(descriptions)
                    .dealer(supplierGameMetaDealer)
                    .links(links)
                    .operationHours(supplierGameMetaHours)
                    .seats(gameTable.getSeats())
                    .betBehind(gameTable.getBetBehind())
                    .results(supplierGameMetaResultsList)
                    .dealerHand(buildStringValueFromIntegersArray(gameTable.getDealerHand()))
                    .seatsTaken(buildStringValueFromIntegersArray(gameTable.getSeatsTaken()))
//                    .history(buildStringValueFromStringsArray(gameTable.getHistory()))
                    .build();
            supplierGameMetaDataList.add(supplierGameMetaData);
        });

        return SupplierGameMetaDataMessage.builder()
                .playersOnline(state.getPlayers())
                .supplierGameMetaData(supplierGameMetaDataList)
                .domainName(domainName)
                .build();
    }

    private List<SupplierGameMetaResults> buildSupplierGameMetaResults(List<Road> road) {
        if (road == null || road.isEmpty())
            return new ArrayList<>();
        List<SupplierGameMetaResults> results = new ArrayList<>();
        road.stream().forEach(r -> {
            SupplierGameMetaResults result = SupplierGameMetaResults.builder()
                    .color(r.getColor())
                    .location(r.getLocation() != null ? r.getLocation().toString() : null)
                    .score(r.getScore())
                    .ties(r.getTies())
                    .playerPair(r.getPlayerPair())
                    .bankerPair(r.getBankerPair())
                    .natural(r.getNatural())
                    .build();
            results.add(result);
        });
        return results;
    }

    private List<SupplierGameMetaResults> buildSupplierGameMetaResults(String stateString, String tableId) {
        JSONObject jsonObjectState = new JSONObject(stateString);
        JSONObject jsonObjectTable = jsonObjectState.getJSONObject("tables").getJSONObject(tableId);

        List<SupplierGameMetaResults> results = new ArrayList<>();

        if (jsonObjectTable.opt("results") != null) {
            JSONArray resultsArray = jsonObjectTable.getJSONArray("results");
            if (resultsArray.get(0).getClass().equals(String.class) || resultsArray.get(0).getClass().equals(Integer.class)) {
                for (int i = 0; i < resultsArray.length(); i++) {
                    SupplierGameMetaResults result = SupplierGameMetaResults.builder()
                            .value(resultsArray.get(i).toString()).build();
                    results.add(result);
                }
            } else if (resultsArray.get(0).getClass().equals(JSONObject.class)) {
                for (int i = 0; i < resultsArray.length(); i++) {
                    SupplierGameMetaResults result = SupplierGameMetaResults.builder()
                            .shield(resultsArray.getJSONObject(i).optBoolean("shield"))
                            .payoutLevel(resultsArray.getJSONObject(i).optString("payoutLevel"))
                            .multiplier(resultsArray.getJSONObject(i).optInt("multiplier"))
                            .value(resultsArray.getJSONObject(i).optString("value"))
                            .build();
                    results.add(result);
                }
            } else if (resultsArray.get(0).getClass().equals(JSONArray.class)) {

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONArray resultChildJsonArray = resultsArray.getJSONArray(i);
                    SupplierGameMetaResults resultsChild = SupplierGameMetaResults.builder()
                            .results(new ArrayList<>())
                            .build();
                    for (int c = 0; c < resultChildJsonArray.length(); c++) {
                        SupplierGameMetaResults result = SupplierGameMetaResults.builder()
                                .shield(resultChildJsonArray.getJSONObject(c).optBoolean("shield"))
                                .payoutLevel(resultChildJsonArray.getJSONObject(c).optString("payoutLevel"))
                                .multiplier(resultChildJsonArray.getJSONObject(c).optInt("multiplier"))
                                .value(resultChildJsonArray.getJSONObject(c).optString("value"))
                                .build();
                        resultsChild.getResults().add(result);
                    }
                    results.add(resultsChild);
                }
            }
        }
        return results;
    }

    private String buildStringValueFromIntegersArray(List<Integer> integers) {
        if (integers == null || integers.isEmpty())
            return null;
        return integers.stream().map(Object::toString)
                .collect(Collectors.joining(","));
    }

    private String buildStringValueFromStringsArray(List<String> strings) {
        if (strings == null || strings.isEmpty())
            return null;
        return String.join(",", strings);
    }

    private SupplierGameMetaDealer buildSupplierGameMetaDealer(Dealer dealer) {
        if (dealer == null) return null;
        return SupplierGameMetaDealer.builder()
                .dealerId(dealer.getDealerId())
                .name(dealer.getName())
                .build();
    }

    private List<SupplierGameMetaLinks> buildSupplierGameMetaLinksList(VideoSnapshot videoSnapshot) {
        if (videoSnapshot == null)
            return new ArrayList<>();
        List<SupplierGameMetaLinks> supplierGameMetaLinkList = new ArrayList<>();

        if (videoSnapshot.getLinks() != null) {
            videoSnapshot.getLinks().keySet().forEach(key -> {
                String link = videoSnapshot.getLinks().get(key);
                SupplierGameMetaLinks supplierGameMetaLink = SupplierGameMetaLinks.builder()
                        .type("link")
                        .url(link)
                        .size(key)
                        .build();
                supplierGameMetaLinkList.add(supplierGameMetaLink);

            });
        }

        if (videoSnapshot.getThumbnails() != null) {
            videoSnapshot.getThumbnails().keySet().forEach(key -> {
                String link = videoSnapshot.getThumbnails().get(key);
                SupplierGameMetaLinks supplierGameMetaLink = SupplierGameMetaLinks.builder().type("thumbnail").url(link).size(key).build();
                supplierGameMetaLinkList.add(supplierGameMetaLink);
            });
        }
        return supplierGameMetaLinkList;
    }

    private List<SupplierGameMetaBetLimit> buildSupplierGameMetaBetLimitsList(Map<String, BetLimits> betLimitsMap) {
        if (betLimitsMap == null)
            return new ArrayList<>();
        List<SupplierGameMetaBetLimit> supplierGameMetaBetLimits = new ArrayList<>();
        betLimitsMap.keySet().forEach(key -> {
            BetLimits betLimit = betLimitsMap.get(key);
            if (betLimit != null) {
                SupplierGameMetaBetLimit supplierGameMetaBetLimit = SupplierGameMetaBetLimit.builder()
                        .currencyCode(key)
                        .currencySymbol(betLimit.getSymbol())
                        .minimum(betLimit.getMin())
                        .maximum(betLimit.getMax())
                        .build();
                supplierGameMetaBetLimits.add(supplierGameMetaBetLimit);
            }
        });
        return supplierGameMetaBetLimits;
    }

    private Set<SupplierGameMetaDescription> buildSupplierGameMetaDescription(Map<String, String> descriptionsMap) {
        Set<SupplierGameMetaDescription> supplierGameMetaDescriptions = new HashSet<>();
        if (descriptionsMap.get("en") != null) {
            SupplierGameMetaDescription supplierGameMetaDescription = SupplierGameMetaDescription.builder()
                    .description(descriptionsMap.get("en"))
                    .language("en")
                    .build();
            supplierGameMetaDescriptions.add(supplierGameMetaDescription);
        }
        return supplierGameMetaDescriptions;
    }

    private SupplierGameMetaHours buildSupplierGameMetaHours(OperationHours operationHours) {
        if (operationHours != null) {
            String startTime = null;
            String endTime = null;
            if (operationHours.getValue() != null) {
                startTime = operationHours.getValue().get("startTime");
                endTime = operationHours.getValue().get("endTime");
            }
            return SupplierGameMetaHours.builder()
                    .type(operationHours.getType())
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();

        }
        return null;
    }

}
