package lithium.service.changelog.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AccountNotesMigrationPrepQueueSink {
  String ORIGINAL_QUEUE = "account-notes-migration-prep-queue.account-notes-migration-prep-group";

  String INPUT = "account-notes-migration-prep-input";

  String DLQ = ORIGINAL_QUEUE+".dlq";
  String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

  @Input(INPUT)
  SubscribableChannel inputChannel();
}
