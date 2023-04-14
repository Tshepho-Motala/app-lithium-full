package lithium.service.accounting.domain.v2.stream;

import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AsyncLabelValueQueueSink {
    String ORIGINAL_QUEUE = "asynclabelvaluequeuev2.asynclabelvaluegroupv2";

    String X_RETRIES_HEADER = "x-retries";
    String X_ORIGINAL_EXCHANGE_HEADER = RepublishMessageRecoverer.X_ORIGINAL_EXCHANGE;
    String X_ORIGINAL_ROUTING_KEY_HEADER = RepublishMessageRecoverer.X_ORIGINAL_ROUTING_KEY;

    String INPUT = "asynclabelvalueinputv2";
    String DLQ = ORIGINAL_QUEUE + ".dlq";
    String PARKING_LOT = ORIGINAL_QUEUE + ".parkingLot";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
