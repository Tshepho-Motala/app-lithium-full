package lithium.service.games.controllers.system;

import lithium.service.games.data.entities.GameUserStatus;
import lithium.service.games.exceptions.Status406NoGamesEnabledException;
import lithium.service.games.services.GameUserStatusService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/system/game-user-status")
public class GamesUserStatusController {

    @Autowired
    private GameUserStatusService gameUserStatusService;

    @GetMapping("find-by-user")
    public List<GameUserStatus> findByUser(@RequestParam String userGuid, LithiumTokenUtil tokenUtil) throws Status406NoGamesEnabledException {
        return gameUserStatusService.findUnlockedFreeGamesForUser(userGuid, tokenUtil);
    }

    @GetMapping("free-games/find/unlocked")
    public List<GameUserStatus> findUnlockedFreeGamesForUser(@RequestParam String userGuid, LithiumTokenUtil tokenUtil) throws Status406NoGamesEnabledException {
        return gameUserStatusService.findUnlockedFreeGamesForUser(userGuid, tokenUtil);
    }

    @PostMapping("free-games/unlock")
    public void unlockFreeGames(@RequestParam  String userGuid, LithiumTokenUtil tokenUtil) throws Status406NoGamesEnabledException, UserClientServiceFactoryException {
        gameUserStatusService.unlockAllFreeGames(userGuid, tokenUtil);
    }

}
