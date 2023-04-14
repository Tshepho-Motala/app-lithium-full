package lithium.service.cashier.stream;

import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface UserPaymentOptionsMigrationQueueSink {
    String ORIGINAL_QUEUE = "user-payment-options-migration-queue.user-payment-options-migration-group";

    String X_RETRIES_HEADER = "x-retries";
    String X_ORIGINAL_EXCHANGE_HEADER = RepublishMessageRecoverer.X_ORIGINAL_EXCHANGE;
    String X_ORIGINAL_ROUTING_KEY_HEADER = RepublishMessageRecoverer.X_ORIGINAL_ROUTING_KEY;

    String INPUT = "user-payment-options-migration-input";
    String DLQ = ORIGINAL_QUEUE + ".dlq";
    String PARKING_LOT = ORIGINAL_QUEUE + ".parkingLot";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
