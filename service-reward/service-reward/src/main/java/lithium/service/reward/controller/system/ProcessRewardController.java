package lithium.service.reward.controller.system;

import lithium.metrics.TimeThisMethod;
import lithium.service.Response.Status;
import lithium.service.reward.client.RewardProviderClient;
import lithium.service.reward.client.exception.Status505UnavailableException;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.service.ProcessRewardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping( "/system" )
public class ProcessRewardController implements RewardProviderClient {

    @Autowired
    ProcessRewardService processRewardService;

  @TimeThisMethod
  @PostMapping( "/reward/process/v1" )
  public ProcessRewardResponse processGiveReward(@RequestBody ProcessRewardRequest request) throws Exception {
    log.debug("Received request to process reward: " + request);
    return processRewardService.process(request);
  }

  @Override
  @TimeThisMethod
  @PostMapping( "/reward/cancel/v1" )
  public CancelRewardResponse processCancelReward(CancelRewardRequest request)
  throws Exception
  {
    log.debug("Received request to cancel reward: {}", request);
    //TODO: take away real money awarded?
    return CancelRewardResponse.builder().code(Status.OK.id() + "").build();
  }
}