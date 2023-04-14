package lithium.service.casino.stream.migration;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CasinoBetsMigrationQueueSink {
  String ORIGINAL_QUEUE = "casino-bets-migration-queue.casino-bets-migration-group";

  String INPUT = "casino-bets-migration-input";

  String DLQ = ORIGINAL_QUEUE+".dlq";
  String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

  @Input(INPUT)
  SubscribableChannel inputChannel();
}
