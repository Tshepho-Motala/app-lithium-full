package lithium.service.games.stream;

import lithium.service.games.client.objects.ProgressiveJackpotFeedRegistration;
import lithium.service.games.services.ProgressiveJackpotFeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding(ProgressiveJackpotFeedRegistrationQueueSink.class)
public class ProgressiveJackpotFeedRegistrationQueueProcessor {

    @Autowired
    ProgressiveJackpotFeedService progressiveJackpotFeedService;

    @StreamListener(ProgressiveJackpotFeedRegistrationQueueSink.INPUT)
    void handle(ProgressiveJackpotFeedRegistration progressiveJackpotFeedRegistration) {
        log.info("Received progressive jackpot registration via queue:" + progressiveJackpotFeedRegistration);
        try {
            progressiveJackpotFeedService.saveProgressiveJackpotFeed(progressiveJackpotFeedRegistration);
        } catch (Exception e) {
            log.error("Failed to process progressive jackpot registration: {}, {}", progressiveJackpotFeedRegistration, e.getMessage(), e);
        }

    }
}
