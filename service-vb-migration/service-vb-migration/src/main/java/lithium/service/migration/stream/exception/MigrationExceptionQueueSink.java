package lithium.service.migration.stream.exception;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MigrationExceptionQueueSink {

  String ORIGINAL_QUEUE = "migration-exception-queue.migration-exception-group";
  String INPUT = "migration-exception-input";
  String DLQ = ORIGINAL_QUEUE + ".dlq";
  String PARKING_LOT = ORIGINAL_QUEUE + ".parking-lot";

  @Input(INPUT)
  SubscribableChannel inputChannel();


}
