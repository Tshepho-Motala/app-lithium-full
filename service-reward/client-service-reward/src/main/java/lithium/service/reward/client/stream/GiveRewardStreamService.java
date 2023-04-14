package lithium.service.reward.client.stream;

import lithium.service.reward.client.dto.GiveRewardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class GiveRewardStreamService {

  @Autowired
  private GiveRewardStreamOutputQueue channel;

  public void giveReward(GiveRewardRequest request) {
    channel.channel().send(MessageBuilder.withPayload(request).build());
  }
}
