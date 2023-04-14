package lithium.service.reward.client.stream;

import lithium.service.reward.client.dto.RewardType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class RewardTypeStream {

  @Autowired
  private RewardTypeStreamOutputQueue channel;

  public void registerRewardType(RewardType rewardType) {
    channel.channel().send(MessageBuilder.withPayload(rewardType).build());
  }
}
