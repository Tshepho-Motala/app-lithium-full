package lithium.service.access.provider.gamstop.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface GamstopQueueSink {
    String INPUT = "gamstopinput";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
