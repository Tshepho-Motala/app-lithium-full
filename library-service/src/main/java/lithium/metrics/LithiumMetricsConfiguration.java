package lithium.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.time.Duration;

@Configuration
@ComponentScan
@EnableConfigurationProperties(LithiumMetricsConfigurationProperties.class)
public class LithiumMetricsConfiguration {

    @ConditionalOnProperty(name = "management.metrics.export.logging.enabled", havingValue = "true")
    @Configuration
    @EnableConfigurationProperties(LoggingMeterRegistryProperties.class)
    @Slf4j
    @AllArgsConstructor
    public static class LoggingMeterRegistryConfiguration{
        private final LoggingMeterRegistryProperties loggingMeterRegistryProperties;

        @Bean
        public LoggingMeterRegistry loggingMeterRegistry() {
            return new LoggingMeterRegistry(new LoggingRegistryConfig() {
                @Override
                public String get(String key) {
                    return null;
                }
                @Override
                public Duration step() {
                    return loggingMeterRegistryProperties.getStep();
                }
            }, Clock.SYSTEM);
        }
    }
}
