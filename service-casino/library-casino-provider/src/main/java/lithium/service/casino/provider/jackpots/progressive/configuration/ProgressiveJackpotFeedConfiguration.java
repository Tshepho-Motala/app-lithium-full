package lithium.service.casino.provider.jackpots.progressive.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lithium.service.casino.provider.jackpots.progressive")
@Data
public class ProgressiveJackpotFeedConfiguration {
    private int timeoutInSeconds = 30;
}
