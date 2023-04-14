package lithium.service.stats.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface DomainStatsQueueSink {
    String INPUT = "domain-stats-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
