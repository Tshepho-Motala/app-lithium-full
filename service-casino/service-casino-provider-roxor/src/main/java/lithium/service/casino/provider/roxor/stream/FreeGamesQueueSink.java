package lithium.service.casino.provider.roxor.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface FreeGamesQueueSink {
    String INPUT = "free-games-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
