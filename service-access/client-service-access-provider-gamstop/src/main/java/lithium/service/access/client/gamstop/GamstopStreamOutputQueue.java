package lithium.service.access.client.gamstop;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface GamstopStreamOutputQueue {
    @Output("gamstopoutput")
    public MessageChannel channel();
}
