package lithium.service.changelog.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AccountNotesMigrationQueueSink {
  String ORIGINAL_QUEUE = "account-notes-migration-queue.account-notes-migration-group";

  String INPUT = "account-notes-migration-input";

  String DLQ = ORIGINAL_QUEUE+".dlq";
  String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

  @Input(INPUT)
  SubscribableChannel inputChannel();
}
