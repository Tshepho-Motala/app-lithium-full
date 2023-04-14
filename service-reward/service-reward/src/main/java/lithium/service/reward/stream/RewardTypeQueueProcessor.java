package lithium.service.reward.stream;

import lithium.service.reward.client.dto.RewardType;
import lithium.service.reward.service.RewardTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding( {RewardTypeQueueSinkV1.class} )
public class RewardTypeQueueProcessor {

  @Autowired
  private RewardTypeService rewardTypeService;

  @StreamListener( RewardTypeQueueSinkV1.INPUT )
  void handleRewardTypeV1(RewardType rewardType) throws Exception {
    log.debug("Received a reward type from the v1 queue for processing: " + rewardType);

    rewardTypeService.register(rewardType);
  }
}
