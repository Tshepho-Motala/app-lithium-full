package lithium.service.reward.controller.system;


import lithium.service.reward.client.ProviderGamesClient;
import lithium.service.reward.client.dto.Game;
import lithium.service.reward.service.ProviderGamesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SystemProviderGamesController implements ProviderGamesClient {

    private final ProviderGamesService providerGamesService;

    @Override
    public List<Game> providerGames(String domainName) throws Exception {
        return providerGamesService.getDomainGames(domainName);
    }

    @Override
    public List<Game> providerGamesForComponent(String domainName, String rewardComponent) throws Exception {
        return providerGamesService.getDomainGames(domainName);
    }
}
