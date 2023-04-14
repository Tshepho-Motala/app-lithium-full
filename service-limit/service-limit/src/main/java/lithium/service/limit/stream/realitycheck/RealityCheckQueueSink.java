package lithium.service.limit.stream.realitycheck;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface RealityCheckQueueSink {

    String ORIGINAL_QUEUE = "reality-check-migration-queue.reality-check-migration-group";

    String INPUT = "reality-check-migration-input";

    String DLQ = ORIGINAL_QUEUE+".dlq";

    String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

    @Input(INPUT)
    SubscribableChannel inputChannel();

}
