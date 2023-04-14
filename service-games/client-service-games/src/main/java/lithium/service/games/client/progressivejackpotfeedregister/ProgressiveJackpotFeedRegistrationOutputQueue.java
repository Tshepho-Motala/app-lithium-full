package lithium.service.games.client.progressivejackpotfeedregister;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProgressiveJackpotFeedRegistrationOutputQueue {
    @Output("progressive-jackpot-feed-registration-out")
    public MessageChannel outputQueue();
}
