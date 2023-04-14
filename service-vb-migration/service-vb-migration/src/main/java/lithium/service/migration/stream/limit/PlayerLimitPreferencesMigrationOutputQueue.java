package lithium.service.migration.stream.limit;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PlayerLimitPreferencesMigrationOutputQueue {

  @Output("player-limit-preferences-migration-output")
  MessageChannel PlayerLimitPreferencesMigrationOutputStream();

}
