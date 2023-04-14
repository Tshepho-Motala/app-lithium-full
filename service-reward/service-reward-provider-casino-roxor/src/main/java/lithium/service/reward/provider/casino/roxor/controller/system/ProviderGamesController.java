package lithium.service.reward.provider.casino.roxor.controller.system;

import lithium.service.reward.client.ProviderGamesClient;
import lithium.service.reward.client.dto.Game;
import lithium.service.reward.provider.casino.roxor.RewardTypeName;
import lithium.service.reward.provider.casino.roxor.service.ProviderGamesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProviderGamesController implements ProviderGamesClient {

  @Autowired
  private ProviderGamesService providerGamesService;

  @Override
  public List<Game> providerGames(String domainName) throws Exception {
    log.debug("get games in domain {}", domainName);
    return providerGamesService.getProviderGames(domainName, null);
  }

  @Override
  public List<Game> providerGamesForComponent(String domainName, String rewardComponent) throws Exception {
    log.debug("get games in domain {} and type {}", domainName, rewardComponent);
    return providerGamesService.getProviderGames(domainName, RewardTypeName.fromName(rewardComponent));
  }
}
