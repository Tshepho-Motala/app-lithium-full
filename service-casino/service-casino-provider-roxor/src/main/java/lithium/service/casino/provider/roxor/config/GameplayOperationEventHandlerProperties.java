package lithium.service.casino.provider.roxor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "lithium.services.casino.provider.roxor.message-handlers.gameplay-operation-event.rabbitmq.listener.retry")
public class GameplayOperationEventHandlerProperties {
    private Long initialInterval;
    private Long multiplier;
    private Long maxInterval;
    private int maxAttempts;
    private int listenerConcurrency;
}
