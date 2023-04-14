package lithium.service.reward.controller.backoffice;

import lithium.service.games.client.service.GamesInternalClientService;
import lithium.service.reward.client.dto.Game;
import lithium.service.reward.service.ProviderGamesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/backoffice/{domainName}/games")
public class BackofficeProviderGameController {

  @Autowired
  private ProviderGamesService providerGamesService;

  //Changed this to a post request because of item 25 https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/2438791178/Lithium+Code+Quality+Guidelines
  @GetMapping(path = "/{provider}")
  public List<Game> getGames(@PathVariable(value = "domainName") String domainName,
                             @PathVariable(value = "provider") String provider) throws Exception {
    log.debug("get games for provider {} in domain {}", provider, domainName);

    return providerGamesService.listProviderGames(domainName, provider, null);
  }

  @GetMapping(path = "/{provider}/{rewardComponent}")
  public List<Game> getGamesForRewardComponent(
          @PathVariable(value = "domainName") String domainName,
          @PathVariable(value = "provider") String provider,
          @PathVariable(value = "rewardComponent") String component) throws Exception {
    log.debug("get games for provider {} in domain {}", provider, domainName);

    return providerGamesService.listProviderGames(domainName, provider, component);
  }
}
