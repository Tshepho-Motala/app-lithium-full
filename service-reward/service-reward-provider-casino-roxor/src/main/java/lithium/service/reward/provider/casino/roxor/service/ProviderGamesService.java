package lithium.service.reward.provider.casino.roxor.service;

import lithium.service.games.client.service.GamesInternalClientService;
import lithium.service.reward.client.dto.Game;
import lithium.service.reward.provider.casino.roxor.RewardTypeName;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class ProviderGamesService {

  @Autowired
  private GamesInternalClientService gamesInternalClientService;

  @Autowired
  private ModelMapper modelMapper;

  public List<Game> getProviderGames(String domainName, RewardTypeName rewardTypeName){
    Stream<lithium.service.games.client.objects.Game> gamesStream = gamesInternalClientService.getGamesForDomainAndProvider(domainName,
            "service-casino-provider-roxor")
            .stream().filter(lithium.service.games.client.objects.Game::isEnabled);

    List<lithium.service.games.client.objects.Game> games = null;

    if (rewardTypeName != null) {
      games = switch (rewardTypeName) {
        case FREESPIN -> gamesStream.filter(g -> Optional.ofNullable(g.getFreeSpinEnabled()).orElse(false))
                .toList();
        case INSTANT_REWARD -> gamesStream.filter(g -> Optional.ofNullable(g.getInstantRewardEnabled()).orElse(false))
                .toList();
        case INSTANT_REWARD_FREESPIN -> gamesStream.filter(g -> Optional.ofNullable(g.getInstantRewardFreespinEnabled()).orElse(false))
                .toList();
        case CASINO_CHIP -> gamesStream.filter(g -> Optional.ofNullable(g.getCasinoChipEnabled()).orElse(false))
                .toList();
      };
    } else {
      games = gamesStream.toList();
    }
    return modelMapper.map(games, new TypeToken<List<Game>>(){}.getType());
  }
}
