package lithium.service.promo.pr.casino.iforium.service;

import lithium.service.games.client.objects.Game;
import lithium.service.games.client.service.GamesInternalClientService;
import lithium.service.promo.client.dto.FieldData;
import lithium.service.promo.pr.casino.iforium.dto.ExtraFieldType;
import lithium.service.promo.pr.casino.iforium.dto.GameProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FieldDataService {

    @Autowired
    private GamesInternalClientService gamesInternalClientService;

    public List<FieldData> getFieldDataForType(String domainName, String provider, ExtraFieldType type) {

        if (type != null) {
            return switch (type) {
                case GAME_PROVIDER -> getGameProviders();
                case GAME -> gamesInternalClientService.getGamesForDomainAndProvider(domainName, provider)
                        .stream().filter(Game::isEnabled)
                                .map(game -> FieldData.builder()
                                .label(game.getName())
                                .value(game.getGuid())
                                .build())
                        .collect(Collectors.toList());
                case GAME_TYPE -> gamesInternalClientService.getGameTypesForDomain(domainName)
                        .stream().map(t -> FieldData.builder()
                                .label(t.getName())
                                .value(t.getName())
                                .build())
                        .collect(Collectors.toList());
            };
        }

        return new ArrayList<>();
    }

    public List<FieldData> getGameProviders() {
        return Arrays.stream(GameProvider.values()).map(provider -> FieldData.builder()
                .label(provider.getProviderName())
                .value(provider.getProviderId())
                .build()).toList();
    }
}
