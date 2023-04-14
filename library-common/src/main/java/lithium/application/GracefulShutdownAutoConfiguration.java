package lithium.application;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration will be conditional executed if the spring-boot-actuator
 * package is present.
 */
@Configuration
public class GracefulShutdownAutoConfiguration {
    @Bean
    HealthIndicator gracefulShutdownHealthCheck() {
        return new GracefulShutdownHealthCheck();
    }
}
