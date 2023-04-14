package lithium.service.casino.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface FreeGameStreamOutputQueue {
    @Output("free-games-output")
    public MessageChannel outputQueue();
}
