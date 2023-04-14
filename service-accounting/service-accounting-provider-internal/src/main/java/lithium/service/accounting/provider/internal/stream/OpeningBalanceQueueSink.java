package lithium.service.accounting.provider.internal.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface OpeningBalanceQueueSink {

    String ORIGINAL_QUEUE = "opening-balance-migration-queue.opening-balance-migration-group";

    String INPUT = "opening-balance-migration-input";

    String DLQ = ORIGINAL_QUEUE+".dlq";
    String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

    @Input(INPUT)
    SubscribableChannel inputChannel();

}
