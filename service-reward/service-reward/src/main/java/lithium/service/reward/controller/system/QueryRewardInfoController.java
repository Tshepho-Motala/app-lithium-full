package lithium.service.reward.controller.system;//package lithium.service.reward.controller.system;

import lithium.metrics.TimeThisMethod;
import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import lithium.service.reward.service.PlayerRewardHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping( "/system" )
public class QueryRewardInfoController {

  @Autowired
  PlayerRewardHistoryService playerRewardHistoryService;
  @Autowired
  ModelMapper modelMapper;

  @TimeThisMethod
  @GetMapping( "/reward/query/v1" )
  public PlayerRewardTypeHistory findByRewardTypeReference(@RequestParam( "rewardTypeReference" ) String rewardTypeReference)
  throws Exception
  {
    log.debug("Request for: " + rewardTypeReference);
    PlayerRewardTypeHistory playerRewardTypeHistory = playerRewardHistoryService.findByRewardTypeReferenceAndConvert(rewardTypeReference);
    log.debug("PlayerRewardTypeHistory: " + playerRewardTypeHistory);
    return playerRewardTypeHistory;
  }

  @TimeThisMethod
  @GetMapping( "/reward/query/v1/{playerRewardTypeHistoryId}" )
  public PlayerRewardTypeHistory findById(@PathVariable( "playerRewardTypeHistoryId" ) Long playerRewardTypeHistoryId)
  throws Exception
  {
    log.debug("Request for PlayerRewardTypeHistory Id: " + playerRewardTypeHistoryId);
    PlayerRewardTypeHistory playerRewardTypeHistory = playerRewardHistoryService.findTypeHistoryById(playerRewardTypeHistoryId);
    log.debug("PlayerRewardTypeHistory: " + playerRewardTypeHistory);
    return playerRewardTypeHistory;
  }
}