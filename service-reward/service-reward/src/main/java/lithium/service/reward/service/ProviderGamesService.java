package lithium.service.reward.service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.games.client.service.GamesInternalClientService;
import lithium.service.reward.client.ProviderGamesClient;
import lithium.service.reward.client.dto.Game;
import lithium.service.reward.client.exception.Status505UnavailableException;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProviderGamesService {

  @Autowired
  private LithiumServiceClientFactory serviceClientFactory;

  @Autowired
  private GamesInternalClientService gamesInternalClientService;

  @Autowired
  private ModelMapper modelMapper;

  public List<Game> listProviderGames(String domainName, String provider, String component) throws Exception {

    if(StringUtil.isEmpty(component)) {
      return getProviderGamesClient(provider).providerGames(domainName);
    }
    return getProviderGamesClient(provider).providerGamesForComponent(domainName, component);
  }

  public List<Game> getDomainGames(String domainName) throws Exception {
      List<lithium.service.games.client.objects.Game> games = gamesInternalClientService.getGamesForDomain(domainName)
              .stream().filter(lithium.service.games.client.objects.Game::isEnabled)
              .toList();
      return modelMapper.map(games, new TypeToken<List<Game>>(){}.getType());
  }

  private ProviderGamesClient getProviderGamesClient(String providerUrl) throws Status505UnavailableException {
    ProviderGamesClient providerGamesClient;
    try {
      providerGamesClient = serviceClientFactory.target(ProviderGamesClient.class, providerUrl, true);
    } catch (Exception e) {
      log.error("Provider unavailable", e);
      throw new Status505UnavailableException(e.getMessage());
    }
    return providerGamesClient;
  }
}
