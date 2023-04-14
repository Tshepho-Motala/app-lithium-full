package lithium.service.user.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UserAttributesTriggerOutputQueue {

  @Output("userattributestriggeroutput")
  public MessageChannel outputQueue();
}


