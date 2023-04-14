package lithium.service.migration.stream.casino;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CasinoGameMigrationOutputQueue {
  @Output("casino-game-migration-output")
  MessageChannel CasinoGameMigrationOutputStream();
}
