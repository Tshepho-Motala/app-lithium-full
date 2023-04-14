package lithium.service.csv.provider.configuration;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.csv.provider.services.CsvProviderAdapter;
import lithium.service.csv.provider.services.CsvService;
import lithium.service.csv.provider.services.GenerationJobService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsvProviderCommonBeansConfiguration {
    @Bean
    public CsvService csvService() {
        return new CsvService();
    }

    @Bean
    public GenerationJobService generationJobService(LithiumServiceClientFactory lithiumServiceClientFactory, CsvProviderAdapter csvProviderAdapter, CsvService csvService) {
        return new GenerationJobService(lithiumServiceClientFactory, csvProviderAdapter, csvService);
    }

}
