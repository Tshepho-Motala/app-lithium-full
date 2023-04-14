package lithium.service.games.stream.migration;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CasinoGameMigrationQueueSink {
  String ORIGINAL_QUEUE = "casino-game-migration-queue.casino-game-migration-group";

  String INPUT = "casino-game-migration-input";

  String DLQ = ORIGINAL_QUEUE+".dlq";
  String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

  @Input(INPUT)
  SubscribableChannel inputChannel();
}
