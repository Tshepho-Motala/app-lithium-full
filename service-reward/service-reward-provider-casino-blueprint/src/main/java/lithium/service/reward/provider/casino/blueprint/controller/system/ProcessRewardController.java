package lithium.service.reward.provider.casino.blueprint.controller.system;

import lithium.metrics.TimeThisMethod;
import lithium.service.reward.client.RewardProviderClient;
import lithium.service.reward.provider.casino.blueprint.services.CancelRewardService;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.provider.casino.blueprint.services.ProcessRewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping( "/system" )
@RequiredArgsConstructor
public class ProcessRewardController implements RewardProviderClient {


  private final ProcessRewardService processRewardService;
  private final CancelRewardService cancelRewardService;

  @TimeThisMethod
  @PostMapping( "/reward/process/v1" )
  public ProcessRewardResponse processGiveReward(@RequestBody ProcessRewardRequest request) throws Exception {
    log.debug("Received request to process reward: " + request);
    return processRewardService.processReward(request);
  }

  @Override
  @TimeThisMethod
  @PostMapping( "/reward/cancel/v1" )
  public CancelRewardResponse processCancelReward(CancelRewardRequest request)
  throws Exception
  {
    log.debug("Received request to cancel reward: " + request);
    return cancelRewardService.cancelReward(request);
  }
}