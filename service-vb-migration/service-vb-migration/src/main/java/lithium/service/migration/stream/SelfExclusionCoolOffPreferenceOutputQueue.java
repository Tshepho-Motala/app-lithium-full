package lithium.service.migration.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SelfExclusionCoolOffPreferenceOutputQueue {

  @Output("self-exclusion-cool-off-preference-output")
  public MessageChannel selfExclusionCoolOffPreferenceOutputQueue();

}
