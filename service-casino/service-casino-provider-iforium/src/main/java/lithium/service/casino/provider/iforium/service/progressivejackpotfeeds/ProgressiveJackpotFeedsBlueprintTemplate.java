package lithium.service.casino.provider.iforium.service.progressivejackpotfeeds;

import lithium.service.casino.client.objects.Game;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotFeedResponse;
import lithium.service.casino.client.objects.progressivejackpotfeed.ProgressiveJackpotGameBalance;
import lithium.service.casino.exceptions.Status510GeneralCasinoExecutionException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.config.IforiumProviderConfig;
import lithium.service.casino.provider.iforium.config.ProviderConfigProperties;
import lithium.service.casino.provider.iforium.config.ProviderConfigService;
import lithium.service.casino.provider.iforium.enums.IForiumGameSuppliers;
import lithium.service.casino.provider.iforium.model.response.progressivejackpotfeeds.blueprint.BlueprintProgressiveJackpotFeedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProgressiveJackpotFeedsBlueprintTemplate extends ProgressiveJackpotFeedTemplate {
    @Autowired private ProviderConfigService providerConfigService;

    public ProgressiveJackpotFeedResponse getProgressiveJackpotFeed(
            String domainName, List<Game> lithiumGames
    )  {

        try {
            String url = providerConfigService.getBlueprintJackpotUrl(domainName);
            validateProviderConfigs(url);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_XML_VALUE);
            HttpEntity requestEntity = new HttpEntity<>(headers);
            ResponseEntity<BlueprintProgressiveJackpotFeedResponse> response = getRestService().getRestTemplate().exchange(url, HttpMethod.POST, requestEntity, BlueprintProgressiveJackpotFeedResponse.class);
            log.trace("Response:: {}", response);
            if (response != null && response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
                return convertIforiumResponseToStandardApiResponse(response.getBody(), lithiumGames);
            }
        } catch (Status512ProviderNotConfiguredException e) {
            log.warn("Provider Iforium not configured for [domain : " + domainName + "] " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            log.warn("Provider property is missing blueprint jackpot feeds url. [domain: " +  domainName + "] " + e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("iforium blueprint progressive feeds error [domainName=" + domainName
                    + ", gameSupplier=" + getGameSupplier() + "] " +
                    e.getMessage());
            return null;
        }
        return null;
    }

    public ProgressiveJackpotFeedResponse convertIforiumResponseToStandardApiResponse(BlueprintProgressiveJackpotFeedResponse progressiveJackpotFeedIforiumResponse, List<Game> game) {
        List<lithium.service.casino.provider.iforium.model.response.progressivejackpotfeeds.blueprint.Game> progressiveFeeds = progressiveJackpotFeedIforiumResponse.getGame();
        ProgressiveJackpotFeedResponse progressiveJackpotFeedResponse = new ProgressiveJackpotFeedResponse();
        List<ProgressiveJackpotGameBalance> progressiveGameBalanceList = new ArrayList<>();
        for (int j = 0; j < progressiveFeeds.size(); j++) {
            for (int k = 0; k < game.size(); k++) {
                ProgressiveJackpotGameBalance progressiveGameBalance = ProgressiveJackpotGameBalance.builder()
                        .progressiveId(progressiveFeeds.get(j).getId())
                        .amount(BigDecimal.valueOf(Double.valueOf(progressiveFeeds.get(j).getJackpot().getCurrentAmount())))
                        .wonByAmount(progressiveFeeds.get(j).getJackpot().getWonByAmount() != null ? BigDecimal.valueOf(Double.valueOf(progressiveFeeds.get(j).getJackpot().getWonByAmount())) : null)
                        .currencyCode(progressiveJackpotFeedIforiumResponse.getCurrency())
                        .game(game.get(k))
                        .build();
                progressiveGameBalanceList.add(progressiveGameBalance);
            }
        }
        progressiveJackpotFeedResponse.setProgressiveJackpotGameBalances(progressiveGameBalanceList);
        log.debug("Progressive Jackpot Feed Standard API Response: " + progressiveJackpotFeedResponse);

        return progressiveJackpotFeedResponse;
    }


    @Override
    public String getGameSupplier() {
        return IForiumGameSuppliers.BLUEPRINT.getGameSupplierName();
    }

    public void validateProviderConfigs(String url) {
        List<String> missingProperties = new ArrayList<>();
        if (url == null || url.trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.BLUEPRINT_JACKPOT_URL.getName());
        }

        if (!missingProperties.isEmpty()) {
                String missingPropertiesStr = String.join(", ", missingProperties);
                throw new IllegalArgumentException("One or more required configuration properties not set."
                        + " ["+missingPropertiesStr+"]");
        }

    }
}
