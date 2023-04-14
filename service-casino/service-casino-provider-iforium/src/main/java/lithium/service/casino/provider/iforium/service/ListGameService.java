package lithium.service.casino.provider.iforium.service;

import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.games.client.objects.Game;

import java.util.List;

public interface ListGameService {
    List<Game> listGames(String domainName) throws Status512ProviderNotConfiguredException;
}
