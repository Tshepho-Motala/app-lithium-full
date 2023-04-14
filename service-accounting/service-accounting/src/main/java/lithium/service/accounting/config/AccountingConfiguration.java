package lithium.service.accounting.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.AsyncRestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableCaching
public class AccountingConfiguration {
	
	@Bean(name = "lithium.service.accounting.asyncresttemplate")
	public AsyncRestTemplate asyncRestTemplate() {
		AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
		log.info("AsyncRestTemplate :"+asyncRestTemplate);
//		asyncRestTemplate.
		return asyncRestTemplate;
	}
}
