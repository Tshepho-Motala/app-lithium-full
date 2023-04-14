package lithium.service.migration.stream.changelog;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AccountNotesMigrationOutputQueue {

  @Output("account-notes-migration-output")
  public MessageChannel accountNotesOutputQueue();

}
