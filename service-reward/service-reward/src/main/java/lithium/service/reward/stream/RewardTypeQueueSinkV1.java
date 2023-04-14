package lithium.service.reward.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface RewardTypeQueueSinkV1 {

  String INPUT = "rewardtypeinputv1";

  @Input( INPUT )
  SubscribableChannel inputChannel ();

}
