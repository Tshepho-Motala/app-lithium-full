package lithium.service.limit.stream.limit;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface PlayerLimitQueueSink {

    String ORIGINAL_QUEUE = "player-limit-preferences-migration-queue.player-limit-preferences-migration-group";

    String INPUT = "player-limit-preferences-migration-input";
    String DLQ = ORIGINAL_QUEUE+".dlq";
    String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

    @Input(INPUT)
    SubscribableChannel inputChannel();

}
