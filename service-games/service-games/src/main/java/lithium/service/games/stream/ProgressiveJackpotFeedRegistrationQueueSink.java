package lithium.service.games.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ProgressiveJackpotFeedRegistrationQueueSink {

    String INPUT = "progressive-jackpot-registration-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
