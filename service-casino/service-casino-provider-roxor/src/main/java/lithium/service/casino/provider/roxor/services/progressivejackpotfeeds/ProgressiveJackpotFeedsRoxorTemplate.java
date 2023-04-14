package lithium.service.casino.provider.roxor.services.progressivejackpotfeeds;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.modules.ModuleInfo;
import lithium.service.casino.client.objects.Game;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotFeedResponse;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance;
import lithium.service.casino.exceptions.Status510GeneralCasinoExecutionException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.api.schema.progressive.Progressive;
import lithium.service.casino.provider.roxor.api.schema.progressive.ProgressiveByGameKey;
import lithium.service.casino.provider.roxor.api.schema.progressive.ProgressiveByWebsiteResponse;
import lithium.service.casino.provider.roxor.config.ProviderConfig;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.enums.RoxorGameSuppliers;
import lithium.service.casino.provider.roxor.storage.repositories.GameRepository;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.games.client.objects.progressive.ProgressiveBalanceGameData;
import lithium.service.games.client.objects.progressive.ProgressiveBalanceGameProgressiveAmountData;
import lithium.service.games.client.objects.progressive.ProgressiveBalanceGameProgressiveData;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ProgressiveJackpotFeedsRoxorTemplate extends ProgressiveJackpotFeedTemplate{
    @Autowired
    ProviderConfigService providerConfigService;
    @Autowired
    ModuleInfo moduleInfo;
    @Autowired
    CachingDomainClientService cachingDomainClientService;
    @Autowired
    ValidationHelper validationHelper;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    ModelMapper modelMapper;

    public ProgressiveJackpotFeedResponse getProgressiveJackpotFeed(
            String domainName, List<Game> game
    ) throws Status510GeneralCasinoExecutionException {
        ProviderConfig pc = null;
        try {
            pc = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

            if (pc.getProgressiveUrl() != null && !(pc.getProgressiveUrl().isEmpty())) {
                String getProgressiveByWebsiteUrl = pc.getProgressiveUrl() + "/game-configuration-api/v2/progressive/context/" +
                        "frontend/progressive-amounts/website/" + pc.getWebsite();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                HttpEntity requestEntity = new HttpEntity<>(null, headers);

                ResponseEntity<String> response = getRestService().getRestTemplate().getForEntity(getProgressiveByWebsiteUrl, String.class, requestEntity);
                if (response != null && response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
                    ObjectMapper mapper = new ObjectMapper();
                    ProgressiveByWebsiteResponse progressiveResponse = mapper.readValue(response.getBody(), ProgressiveByWebsiteResponse.class);
                    return translateData(domainName, progressiveResponse, pc.getCurrency());
                }
                log.debug("Roxor Progressive Feeds Error Response Headers : {}", response.getHeaders());
                log.debug("Roxor Progressive Feeds Error  Response Body : {}", response.getBody());
            }
        } catch (Status512ProviderNotConfiguredException e) {
            log.warn("Provider Roxor not configured for [domain : " + domainName + "] " + e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("roxor feeds error [domainName=" + domainName
                    + ", website=" + pc.getWebsite() + "] " +
                    ", gameSupplier=" + getGameSupplier() + "] " +
                    e.getMessage());
            return null;
        }
        return null;
    }

    @Override
    public String getGameSupplier() {
        return RoxorGameSuppliers.ROXOR.getGameSupplierName();
    }

    public ProgressiveJackpotFeedResponse convertRoxorResponseToStandardApiResponse(List<ProgressiveBalanceGameData> progressiveBalanceGameData, List<lithium.service.games.client.objects.Game> game) {
        Map<String, lithium.service.games.client.objects.Game> gameMap = game.stream().collect(Collectors.toMap(lithium.service.games.client.objects.Game::getGuid, Function.identity()));
        Map<String, lithium.service.casino.client.objects.Game> gameMapClient = modelMapper.map(gameMap, new TypeToken<Map<String, lithium.service.casino.client.objects.Game>>(){}.getType());
        ProgressiveJackpotFeedResponse progressiveJackpotFeedResponse = new ProgressiveJackpotFeedResponse();
        List<ProgressiveJackpotGameBalance> progressiveGameBalanceList = new ArrayList<>();
        for (int z = 0; z < progressiveBalanceGameData.size(); z++) {
            List<ProgressiveBalanceGameProgressiveData> progressiveAmountsByGameKeys = progressiveBalanceGameData.get(z).getProgressiveBalanceGameProgressiveDataList();
            for (int i = 0; i < progressiveAmountsByGameKeys.size(); i++) {
                List<ProgressiveBalanceGameProgressiveAmountData> progressiveList = progressiveAmountsByGameKeys.get(i).getProgressiveBalanceGameProgressiveAmountDataList();
                for (int j = 0; j < progressiveList.size(); j++) {
                    ProgressiveJackpotGameBalance progressiveGameBalance = ProgressiveJackpotGameBalance.builder()
                                .progressiveId(progressiveAmountsByGameKeys.get(i).getProgressiveId())
                                .amount(progressiveList.get(j).getAmount())
                                .currencyCode(progressiveList.get(j).getCurrency())
                                .game(gameMapClient.get(progressiveBalanceGameData.get(z).getGameGuid()))
                                .build();
                    progressiveGameBalanceList.add(progressiveGameBalance);
                }
            }
        }
        progressiveJackpotFeedResponse.setProgressiveJackpotGameBalances(progressiveGameBalanceList);
        log.debug("Progressive Jackpot Feed Standard API Response: " + progressiveJackpotFeedResponse);
        return progressiveJackpotFeedResponse;
    }

    public ProgressiveJackpotFeedResponse translateData(
            String domainName,
            ProgressiveByWebsiteResponse progressiveByWebsiteResponse,
            String currency
    ) {
        List<ProgressiveBalanceGameData> returnList = new ArrayList<>();
        List<lithium.service.games.client.objects.Game> gameList = new ArrayList<>();
        for (ProgressiveByGameKey progressiveByGameKey : progressiveByWebsiteResponse.getProgressiveAmountsByGameKeys()) {
            lithium.service.games.client.objects.Game lithiumGame;
            try {
                lithiumGame = validationHelper.getGame(
                        new GamePlayContext(),
                        domainName,
                        moduleInfo.getModuleName(),
                        progressiveByGameKey.getGameKey()
                );

                ProgressiveBalanceGameData progressiveBalanceGameData = ProgressiveBalanceGameData
                        .builder()
                        .gameGuid(lithiumGame.getGuid())
                        .progressiveBalanceGameProgressiveDataList(new ArrayList<>())
                        .build();

                for (Progressive progressive : progressiveByGameKey.getProgressiveAmountsById()) {
                    ProgressiveBalanceGameProgressiveData progressiveBalanceGameProgressiveData = ProgressiveBalanceGameProgressiveData
                            .builder()
                            .progressiveId(progressive.getProgressiveId())
                            .progressiveBalanceGameProgressiveAmountDataList(progressive.getProgressiveAmounts().stream().filter(currencyCode -> currencyCode.getCurrency().contentEquals(currency)).map(
                                    o -> ProgressiveBalanceGameProgressiveAmountData
                                            .builder()
                                            .amount(o.getAmount())
                                            .currency(o.getCurrency())
                                            .build()
                            ).collect(Collectors.toList()))
                            .build();
                    progressiveBalanceGameData.getProgressiveBalanceGameProgressiveDataList().add(progressiveBalanceGameProgressiveData);
                }

                returnList.add(progressiveBalanceGameData);
                gameList.add(lithiumGame);
            } catch (Exception e) {
                log.warn("Roxor GameKey [" + progressiveByGameKey.getGameKey() + "] does not exist on Lithium for domain [" + domainName + "]");
                continue;
            }
        }
        return convertRoxorResponseToStandardApiResponse(returnList, gameList);
    }

}
