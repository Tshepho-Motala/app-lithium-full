package lithium.service.reward.client;

import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient( "service-reward" )
public interface PlayerRewardUpdateClient {

  @RequestMapping( path = "/system/reward/player/updatecounter/v1", method = RequestMethod.POST )
  PlayerRewardTypeHistory updatePlayerRewardCounter(@RequestParam("playerRewardTypeHistoryId") Long playerRewardTypeHistoryId)
  throws Exception; //TODO: add specific error code exceptions

}
