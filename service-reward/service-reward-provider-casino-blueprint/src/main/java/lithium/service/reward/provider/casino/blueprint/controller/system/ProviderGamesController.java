package lithium.service.reward.provider.casino.blueprint.controller.system;

import lithium.service.reward.client.ProviderGamesClient;
import lithium.service.reward.client.dto.Game;
import lithium.service.reward.provider.casino.blueprint.services.ProviderGamesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProviderGamesController implements ProviderGamesClient {
    private final ProviderGamesService providerGamesService;

    @RequestMapping( path = "/system/{domainName}/games", method = RequestMethod.GET)
    @Override
    public List<Game> providerGames(String domainName) throws Exception {
        return providerGamesService.getProviderGames(domainName);
    }

    @Override
    public List<Game> providerGamesForComponent(String domainName, String rewardComponent) throws Exception {
        return providerGames(domainName);
    }
}
