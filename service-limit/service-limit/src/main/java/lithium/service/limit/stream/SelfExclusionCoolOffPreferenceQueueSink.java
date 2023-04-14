package lithium.service.limit.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface SelfExclusionCoolOffPreferenceQueueSink {

    String ORIGINAL_QUEUE = "self-exclusion-cool-off-preference-queue.self-exclusion-cool-off-preference-group";

    String INPUT = "self-exclusion-cool-off-preference-input";
    String DLQ = ORIGINAL_QUEUE+".dlq";
    String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

    @Input(INPUT)
    SubscribableChannel inputChannel();

}
