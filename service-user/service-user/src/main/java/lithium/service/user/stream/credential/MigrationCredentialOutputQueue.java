package lithium.service.user.stream.credential;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MigrationCredentialOutputQueue {

  @Output("migration-credential-output")
  MessageChannel migrationCredentialOutputQueue();

}
