package lithium.service.reward.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface GiveRewardStreamOutputQueue {

  @Output( "giverewardoutputv1" )
  MessageChannel channel();
}