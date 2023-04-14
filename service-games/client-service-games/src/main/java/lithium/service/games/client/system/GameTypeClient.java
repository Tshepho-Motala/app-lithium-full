package lithium.service.games.client.system;

import lithium.service.games.client.objects.GameType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "service-games")
public interface GameTypeClient {

    @RequestMapping("/system/game-types/{domainName}")
    List<GameType> getGameTypesForDomain(@PathVariable("domainName") String domainName);
}
