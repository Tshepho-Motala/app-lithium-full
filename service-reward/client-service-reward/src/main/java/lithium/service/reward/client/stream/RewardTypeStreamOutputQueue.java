package lithium.service.reward.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface RewardTypeStreamOutputQueue {

  @Output( "rewardtypeoutputv1" )
  MessageChannel channel ();
}