package lithium.service.games.client;

import lithium.service.games.client.objects.GameUserStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "service-games", path = "/system/game-user-status")
public interface GameUserStatusClient {

    @RequestMapping("/free-games/find/unlocked")
    List<GameUserStatus> findUnlockedFreeGamesForUser(@RequestParam("userGuid") String userGuid);

    @RequestMapping(value = "/free-games/unlock", method = RequestMethod.POST)
    void unlockFreeGames(@RequestParam("userGuid") String userGuid);

    @RequestMapping("/find-by-user")
    List<GameUserStatus> findByUser(String userGuid);
}
