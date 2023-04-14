package lithium.service.user.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface VbMigrationQueueSink {
  String ORIGINAL_QUEUE = "vb-migration-users-queue.vb-migration-users-group";

  String DLQ = ORIGINAL_QUEUE + ".dlq";
  String PARKING_LOT = ORIGINAL_QUEUE + ".parkingLot";
  String INPUT = "vb-migration-input";

  @Input(INPUT)
  SubscribableChannel inputChannel();
}
