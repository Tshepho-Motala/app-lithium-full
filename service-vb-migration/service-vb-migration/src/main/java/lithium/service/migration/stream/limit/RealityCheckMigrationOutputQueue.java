package lithium.service.migration.stream.limit;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface RealityCheckMigrationOutputQueue {

  @Output("reality-check-migration-output")
  MessageChannel RealityCheckMigrationOutputStream();

}
