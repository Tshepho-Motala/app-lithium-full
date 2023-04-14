package lithium.service.datafeed.provider.google.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "publisher")
@Data
public class PublisherExecutorConfig {
    private int executorThreadCount;
}
