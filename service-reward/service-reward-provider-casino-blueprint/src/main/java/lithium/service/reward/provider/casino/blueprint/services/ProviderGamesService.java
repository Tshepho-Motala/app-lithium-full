package lithium.service.reward.provider.casino.blueprint.services;


import lithium.service.games.client.service.GamesInternalClientService;
import lithium.service.reward.client.dto.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderGamesService {
    private final GamesInternalClientService gamesInternalClientService;
    public List<Game> getProviderGames(String domainName){
        List<lithium.service.games.client.objects.Game> games = gamesInternalClientService.getGamesForDomainAndProvider(domainName,
                "service-casino-provider-iforium");
        return transformAndFilterGames(games);
    }

    public List<Game> transformAndFilterGames(List<lithium.service.games.client.objects.Game> games) {
        return games.stream().filter(lithium.service.games.client.objects.Game::isEnabled)
                .map(this::mapToGame)
                .toList();
    }

    public Game mapToGame(lithium.service.games.client.objects.Game game) {
        return Game.builder()
                .name(game.getName())
                .description(game.getDescription())
                .guid(game.getGuid())
                .providerGameId(game.getSupplierGameRewardGuid())
                .commercialName(Optional.ofNullable(game.getCommercialName()).orElse(game.getName()))
                .build();
    }
}
