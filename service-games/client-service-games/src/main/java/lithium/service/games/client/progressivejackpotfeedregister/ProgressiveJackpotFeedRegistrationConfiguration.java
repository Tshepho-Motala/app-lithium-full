package lithium.service.games.client.progressivejackpotfeedregister;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(ProgressiveJackpotFeedRegistrationOutputQueue.class)
@ComponentScan
public class ProgressiveJackpotFeedRegistrationConfiguration {
}
