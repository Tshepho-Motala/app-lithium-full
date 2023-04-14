package lithium.service.casino.provider.jackpots.progressive.rest;

import lithium.service.casino.provider.jackpots.progressive.configuration.ProgressiveJackpotFeedConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
@Data
@Slf4j
public class ProgressiveJackpotFeedRestService {
    private ProgressiveJackpotFeedConfiguration configuration;
    private RestTemplate restTemplate;

    @Autowired
    public ProgressiveJackpotFeedRestService(@Qualifier("lithium.rest") RestTemplateBuilder restTemplateBuilder, ProgressiveJackpotFeedConfiguration configuration) {
        log.trace("ProgressiveJackpotFeedRestService | configuration: {}", configuration);
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(configuration.getTimeoutInSeconds()))
                .build();
        this.configuration = configuration;
    }
}
