package lithium.service.migration.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface VbMigrationOutputQueue {
  @Output("vb-migration-output")
  MessageChannel vbMigrationOutputStream();
}
