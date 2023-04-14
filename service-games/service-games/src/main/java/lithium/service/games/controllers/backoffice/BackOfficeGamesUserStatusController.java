package lithium.service.games.controllers.backoffice;

import lithium.service.games.data.entities.GameUserStatus;
import lithium.service.games.exceptions.Status406NoGamesEnabledException;
import lithium.service.games.services.GameUserStatusService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/backoffice/game-user-status")
public class BackOfficeGamesUserStatusController {

    @Autowired
    private GameUserStatusService gameUserStatusService;

    @ResponseBody
    @GetMapping("/free-games/find/unlocked")
    public List<GameUserStatus> findUnlockedFreeGamesForUser(@RequestParam String userGuid, LithiumTokenUtil tokenUtil) throws Status406NoGamesEnabledException {
        return gameUserStatusService.findUnlockedFreeGamesForUser(userGuid, tokenUtil);
    }

    @ResponseBody
    @PostMapping("/free-games/unlock")
    public void unlockFreeGames(@RequestParam  String userGuid, LithiumTokenUtil tokenUtil) throws Status406NoGamesEnabledException, UserClientServiceFactoryException {
        gameUserStatusService.unlockAllFreeGames(userGuid, tokenUtil);
    }

    @ResponseBody
    @PostMapping("/free-games/lock")
    public void lockFreeGames(@RequestParam  String userGuid, LithiumTokenUtil tokenUtil) throws UserClientServiceFactoryException {
        gameUserStatusService.lockAllFreeGames(userGuid, tokenUtil);
    }
}
