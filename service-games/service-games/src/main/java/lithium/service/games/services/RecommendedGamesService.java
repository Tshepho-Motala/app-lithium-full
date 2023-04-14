package lithium.service.games.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.games.client.objects.RecommendedGameBasic;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameGraphic;
import lithium.service.games.provider.google.rge.client.RecommendedGamesGoogleRgeClient;
import lithium.service.games.provider.google.rge.client.objects.response.Recommendation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendedGamesService {

    @Autowired
    private LithiumServiceClientFactory serviceClientFactory;

    @Autowired
    private GameService gameService;

    @Autowired
    private ProviderClientService providerClientService;

    private RecommendedGamesGoogleRgeClient getRecommendedGamesGoogleRgeClient() throws LithiumServiceClientFactoryException {
        try {
            return serviceClientFactory.target(RecommendedGamesGoogleRgeClient.class, "service-games-provider-google-rge", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public List<RecommendedGameBasic> getRecommendedGames(String userGuid, Boolean liveCasino, String channel, String locale)
            throws LithiumServiceClientFactoryException, Status550ServiceDomainClientException,
            Status474DomainProviderDisabledException, Status500InternalServerErrorException {
        String domainName = userGuid.split("/")[0];
        try {
            providerClientService.checkProviderEnabled(domainName, "service-games-provider-google-rge", locale);
        } catch (Status474DomainProviderDisabledException | Status550ServiceDomainClientException e) {
            log.error("Recommended games provider is not configured for domain. domainName: " + domainName, e);
            throw e;
        }
        List<Recommendation> recommendationList = getRecommendedGamesGoogleRgeClient().getGameRecommendation(userGuid);
        if (recommendationList == null) {
            String errorMsg = "Failed to get recommended games for user, UserGuid: " + userGuid + "domainName: " + domainName;
            log.error(errorMsg);
            throw new Status500InternalServerErrorException(errorMsg);
        }
        Map<String, Recommendation> recommendationMap = recommendationList.stream().collect(Collectors.toMap(Recommendation::getGameGUID, Function.identity()));
        Set<String> gameGuids = recommendationList.stream().map(recommendation -> recommendation.getGameProviderName() + "_" + recommendation.getGameGUID()).collect(Collectors.toSet());
        List<Game> games = gameService.getByGuidsAndDomain(gameGuids, domainName);
        games = games.stream().filter(game -> game.isEnabled() && game.isVisible() && liveCasino.equals(game.getLiveCasino())
                && game.getGameChannels().stream().anyMatch(gameChannel -> gameChannel.getChannel().getName().equalsIgnoreCase(channel)))
                .collect(Collectors.toList());
        List<RecommendedGameBasic> gb = games.stream().map(game -> {
            GameGraphic gameGraphic = null;
            try {
                gameGraphic = gameService.findCdnExternalGraphic(game.getDomain().getName(),
                        game.getId(), liveCasino);
            } catch (Status500InternalServerErrorException e) {
                log.error("Problem trying to retrieve game graphic for recommended game [game=" + game + "] "
                        + e.getMessage(), e);
            }

            String supplierName = (game.getGameSupplier() != null) ? game.getGameSupplier().getName() : null;
            Integer gameRank = recommendationMap.get(game.getProviderGameId()).getGameRank();
            return RecommendedGameBasic.builder()
                    .gameId(game.getGuid())
                    .gameName(game.getCommercialName())
                    .supplierName(supplierName)
                    .image((gameGraphic != null) ? gameGraphic.getUrl() : null)
                    .gameRank(gameRank)
                    .build();
        }).collect(Collectors.toList());
        log.debug("Recommended games List: " + Arrays.toString(gb.toArray()));
        return gb.stream()
                .sorted(Comparator.comparingInt(RecommendedGameBasic::getGameRank))
                .collect(Collectors.toList());
    }
}
