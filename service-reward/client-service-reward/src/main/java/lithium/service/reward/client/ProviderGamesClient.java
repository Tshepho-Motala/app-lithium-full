package lithium.service.reward.client;

import lithium.service.reward.client.dto.Game;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient( "service-reward" )
public interface ProviderGamesClient {

  @RequestMapping( path = "/system/{domainName}/games", method = RequestMethod.GET)
  List<Game> providerGames(@PathVariable(value = "domainName") String domainName) throws Exception;

  @RequestMapping( path = "/system/{domainName}/{rewardComponent}", method = RequestMethod.GET)
  List<Game> providerGamesForComponent(@PathVariable(value = "domainName") String domainName, @PathVariable(value = "rewardComponent") String rewardComponent)
          throws Exception;
}
