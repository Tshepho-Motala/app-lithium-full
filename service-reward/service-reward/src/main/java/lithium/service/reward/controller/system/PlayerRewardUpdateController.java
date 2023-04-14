package lithium.service.reward.controller.system;

import lithium.metrics.TimeThisMethod;
import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import lithium.service.reward.client.exception.Status505UnavailableException;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.service.PlayerRewardHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping( "/system" )
public class PlayerRewardUpdateController {
  @Autowired
  private PlayerRewardHistoryService playerRewardHistoryService;

  @TimeThisMethod
  @PostMapping( "/reward/player/updatecounter/v1" )
  public PlayerRewardTypeHistory updatePlayerRewardCounter(@RequestParam("playerRewardTypeHistoryId") Long playerRewardTypeHistoryId) throws Status505UnavailableException {
    log.debug("Received request to update player reward counter: " + playerRewardTypeHistoryId);

    PlayerRewardTypeHistory prth = playerRewardHistoryService.updateCounter(playerRewardTypeHistoryId);

    return prth;
  }

}
