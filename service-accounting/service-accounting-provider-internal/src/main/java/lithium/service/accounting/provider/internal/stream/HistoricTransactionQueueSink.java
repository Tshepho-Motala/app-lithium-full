package lithium.service.accounting.provider.internal.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface HistoricTransactionQueueSink {
    String ORIGINAL_QUEUE = "historic-migration-queue.historic-migration-group";

    String INPUT = "historic-migration-input";

    String DLQ = ORIGINAL_QUEUE+".dlq";
    String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
