package lithium.service.promo.services;

import lithium.service.reward.client.dto.GiveRewardRequest;
import lithium.service.reward.client.dto.RewardSource;
import lithium.service.reward.client.stream.GiveRewardStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RewardService {

  @Autowired
  GiveRewardStreamService giveRewardStreamService;

  public void triggerReward(String playerGuid, Long rewardId) {
    if (rewardId != null) {
      GiveRewardRequest giveRewardRequest = GiveRewardRequest.builder()
          .playerGuid(playerGuid)
          .rewardId(rewardId)
          .rewardSource(RewardSource.SYSTEM)
          .build();
      log.info("Sending GiveRewardRequest: " + giveRewardRequest);
      //    GiveRewardResponse giveRewardResponse =
      giveRewardStreamService.giveReward(giveRewardRequest);
      log.debug("------------------------------------------------------");
    }
  }
}
