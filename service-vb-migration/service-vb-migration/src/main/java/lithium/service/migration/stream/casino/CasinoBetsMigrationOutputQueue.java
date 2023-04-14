package lithium.service.migration.stream.casino;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CasinoBetsMigrationOutputQueue {
  @Output("casino-bets-migration-output")
  MessageChannel CasinoBetsMigrationOutputStream();
}
