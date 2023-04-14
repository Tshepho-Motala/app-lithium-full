package lithium.service.user.provider.threshold.controller;

import java.util.List;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.services.PlayerThresholdHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/player-threshold-history")
public class PlayerThresholdHistoryController {

  @Autowired
  private PlayerThresholdHistoryService playerThresholdHistoryService;
  @GetMapping("/find-by-playerguid/p")
  public Response<List<PlayerThresholdHistory>> getPlayerNotifications(@RequestParam String playerGuid)
  throws Status500InternalServerErrorException
  {
    List<PlayerThresholdHistory> playerThresholdHistoryList = playerThresholdHistoryService.findByUserGuid(playerGuid);
    return Response.<List<PlayerThresholdHistory>>builder().data(playerThresholdHistoryList).status(Status.OK).build();
  }
}
