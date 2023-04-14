package lithium.service.migration.stream.credential;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MigrationCredentialQueueSink {

  String ORIGINAL_QUEUE = "migration-credential-queue.migration-credential-group";
  String INPUT = "migration-credential-input";
  String DLQ = ORIGINAL_QUEUE + ".dlq";
  String PARKING_LOT = ORIGINAL_QUEUE + ".parking-lot";

  @Input(INPUT)
  SubscribableChannel inputChannel();


}
