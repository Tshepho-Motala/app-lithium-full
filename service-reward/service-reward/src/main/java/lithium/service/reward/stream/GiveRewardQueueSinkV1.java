package lithium.service.reward.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface GiveRewardQueueSinkV1 {

  String INPUT = "giverewardinputv1";

  @Input( INPUT )
  SubscribableChannel inputChannel();

}
