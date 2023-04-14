package lithium.service.user.client.stream;

import lithium.service.user.client.objects.UserAttributesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserAttributesTriggerStream {
  @Autowired
  private UserAttributesTriggerOutputQueue queue;

  public void trigger(UserAttributesData data) {
    queue.outputQueue().send(MessageBuilder.<UserAttributesData>withPayload(data).build());
  }

}
