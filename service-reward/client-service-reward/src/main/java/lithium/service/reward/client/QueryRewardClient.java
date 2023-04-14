package lithium.service.reward.client;

import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient( "service-reward" )
public interface QueryRewardClient {

  @RequestMapping( path = "/system/reward/query/v1", method = RequestMethod.GET )
  @ResponseBody
  PlayerRewardTypeHistory findByRewardTypeReference(@RequestParam( "rewardTypeReference" ) String rewardTypeReference)
  throws Exception; //TODO: add specific error code exceptions

  @RequestMapping( path = "/system/reward/query/v1/{playerRewardTypeHistoryId}", method = RequestMethod.GET )
  PlayerRewardTypeHistory findById(@PathVariable( "playerRewardTypeHistoryId" ) Long playerRewardTypeHistoryId)
  throws Exception;
}
