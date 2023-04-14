package lithium.service.accounting.provider.internal.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MigrationExceptionOutputQueue {

  @Output("migration-exception-output")
  MessageChannel migrationExceptionOutputQueue();


}
