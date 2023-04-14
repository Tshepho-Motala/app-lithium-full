package lithium.service.accounting.domain.summary.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AsyncLabelValueQueueSink {
    String ORIGINAL_QUEUE = "asynclabelvaluequeue.asynclabelvaluegroup";

    String INPUT = "asynclabelvalueinput";
    String DLQ = ORIGINAL_QUEUE + ".dlq";
    String PARKING_LOT = ORIGINAL_QUEUE + ".parkingLot";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
