package lithium.service.promo.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface PromoProviderQueueSinkV1 {

  String INPUT = "promoproviderinputv1";

  @Input( INPUT )
  SubscribableChannel inputChannel ();

}
