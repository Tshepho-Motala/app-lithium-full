package lithium.service.migration.stream.changelog;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AccountNotesMigrationPrepOutputQueue {

  @Output("account-notes-migration-prep-output")
  public MessageChannel accountNotesPrepOutputQueue();

}
