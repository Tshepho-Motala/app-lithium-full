package lithium.service.document.generation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lithium.service.document.generation")
@Data
public class DocumentGenerationConfigurationProperties {
    private long clearDelayMillis;
}
