package lithium.service.casino.provider.roxor.services.progressivejackpotfeeds;

import lithium.modules.ModuleInfo;
import lithium.service.casino.client.objects.Game;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotFeedResponse;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance;
import lithium.service.casino.exceptions.Status510GeneralCasinoExecutionException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.api.schema.progressive.MicroGamingProgressiveJackpotFeedResponse;
import lithium.service.casino.provider.roxor.config.ProviderConfig;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.enums.RoxorGameSuppliers;
import lithium.service.casino.provider.roxor.storage.repositories.GameRepository;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.domain.client.CachingDomainClientService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProgressiveJackpotFeedsMicroGamingTemplate extends ProgressiveJackpotFeedTemplate{

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
            String domainName, List<Game> lithiumGames
    ) throws Status510GeneralCasinoExecutionException {
        ProviderConfig pc = null;
        try {
            pc = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity requestEntity = new HttpEntity<>(null, headers);
            String url = pc.getMicroGamingProgressiveJackpotFeedUrl();
            validateConfiguration(pc);
            ResponseEntity<MicroGamingProgressiveJackpotFeedResponse[]> response = getRestService().getRestTemplate().getForEntity(url, MicroGamingProgressiveJackpotFeedResponse[].class, requestEntity);
            List<MicroGamingProgressiveJackpotFeedResponse> microGamingProgressiveJackpotFeedResponseList = Arrays.asList(response.getBody());
            log.trace("Response:: {}", response);
            if (response != null && response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
                return convertRoxorResponseToStandardApiResponse(microGamingProgressiveJackpotFeedResponseList, lithiumGames);
            }
        } catch (Status512ProviderNotConfiguredException e) {
            log.warn("Provider Roxor not configured for domain : " + domainName);
            return null;
        } catch(IllegalArgumentException illegalArgumentException){
            log.warn("Provider property is missing jackpot feeds url. " + illegalArgumentException.getMessage());
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
        return RoxorGameSuppliers.MICRO_GAMING.getGameSupplierName();
    }

    public ProgressiveJackpotFeedResponse convertRoxorResponseToStandardApiResponse(List<MicroGamingProgressiveJackpotFeedResponse> microGamingProgressiveJackpotFeedResponseList, List<Game> lithiumGames) {
        ProgressiveJackpotFeedResponse progressiveJackpotFeedResponse = new ProgressiveJackpotFeedResponse();
        List<ProgressiveJackpotGameBalance> progressiveGameBalanceList = new ArrayList<>();
        List<Game> filterGameList = new ArrayList<>();
        for (MicroGamingProgressiveJackpotFeedResponse microGamingProgressiveJackpotFeedResponse : microGamingProgressiveJackpotFeedResponseList) {
            filterGameList = lithiumGames.stream().filter(games -> games.getModuleSupplierId() != null && games.getModuleSupplierId().contentEquals(String.valueOf(microGamingProgressiveJackpotFeedResponse.getModuleId()))).collect(Collectors.toList());
            for (Game game : filterGameList) {
                ProgressiveJackpotGameBalance progressiveGameBalance = ProgressiveJackpotGameBalance.builder()
                        .progressiveId(String.valueOf(microGamingProgressiveJackpotFeedResponse.getProgressiveId()))
                        .amount(microGamingProgressiveJackpotFeedResponse.getStartAtValue())
                        .currencyCode(microGamingProgressiveJackpotFeedResponse.getCurrencyIsoCode())
                        .game(game)
                        .build();
                progressiveGameBalanceList.add(progressiveGameBalance);
            }
        }
        progressiveJackpotFeedResponse.setProgressiveJackpotGameBalances(progressiveGameBalanceList);
        log.debug("Progressive Jackpot Feed Standard API Response: " + progressiveJackpotFeedResponse);
        return progressiveJackpotFeedResponse;
    }

    private void validateConfiguration(ProviderConfig providerConfig) {
        if(providerConfig.getMicroGamingProgressiveJackpotFeedUrl() == null || providerConfig.getMicroGamingProgressiveJackpotFeedUrl().isEmpty()){
            throw new IllegalArgumentException("Provider property is missing jackpot feeds url.");
        }
    }

}
