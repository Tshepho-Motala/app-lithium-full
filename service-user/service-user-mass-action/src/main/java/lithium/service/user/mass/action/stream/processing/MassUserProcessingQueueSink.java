package lithium.service.user.mass.action.stream.processing;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MassUserProcessingQueueSink {

    String INPUT = "massuserprocessinginput";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
