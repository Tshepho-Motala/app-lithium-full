package lithium.rest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ComponentScan
@EnableConfigurationProperties(LithiumRestConfigurationProperties.class)
public class LithiumRestTemplateConfiguration {
	@Bean
	@Qualifier("lithiumRestTemplateCustomizer")
	public LithiumRestTemplateCustomizer lithiumRestTemplateCustomizer() {
		return new LithiumRestTemplateCustomizer();
	}

	@Bean
	@DependsOn(value = {"lithiumRestTemplateCustomizer"})
	@Qualifier("lithium.rest")
	public RestTemplateBuilder restTemplateBuilder() {
		return new RestTemplateBuilder(lithiumRestTemplateCustomizer());
	}
}
