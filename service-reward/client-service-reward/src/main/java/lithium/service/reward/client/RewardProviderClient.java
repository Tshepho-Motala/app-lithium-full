package lithium.service.reward.client;

import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient( "service-reward" )
public interface RewardProviderClient {

  @RequestMapping( path = "/system/reward/process/v1", method = RequestMethod.POST )
  ProcessRewardResponse processGiveReward(@RequestBody ProcessRewardRequest request)
  throws Exception; //TODO: add specific error code exceptions

  @RequestMapping( path = "/system/reward/cancel/v1", method = RequestMethod.POST )
  CancelRewardResponse processCancelReward(@RequestBody CancelRewardRequest request)
  throws Exception; //TODO: add specific error code exceptions
}
