package lithium.csv.provider.threshold;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.csv.provider.threshold.config.CsvThresholdProviderConfigurationProperties;
import lithium.leader.EnableLeaderCandidate;
import lithium.metrics.EnableLithiumMetrics;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.csv.provider.EnableCsvGenerationClient;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@LithiumService
@EnableLithiumMetrics
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableConfigurationProperties(CsvThresholdProviderConfigurationProperties.class)
@EnableCsvGenerationClient
public class ServiceCsvThresholdProviderApplication extends LithiumServiceApplication {

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCsvThresholdProviderApplication.class, args);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
