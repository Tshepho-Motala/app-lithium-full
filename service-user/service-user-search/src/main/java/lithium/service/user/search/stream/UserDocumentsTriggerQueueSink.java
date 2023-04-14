package lithium.service.user.search.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface UserDocumentsTriggerQueueSink {
  String INPUT = "userdocumenttriggerinput";

  @Input(INPUT)
  SubscribableChannel inputChannel();
}
