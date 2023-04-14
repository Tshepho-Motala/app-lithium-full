package lithium.service.accounting.provider.internal.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface UpdatingBalanceQueueSink {

    String ORIGINAL_QUEUE = "updating-balance-migration-queue.updating-balance-migration-group";

    String INPUT = "updating-balance-migration-input";

    String DLQ = ORIGINAL_QUEUE+".dlq";
    String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

    @Input(INPUT)
    SubscribableChannel inputChannel();

}
