package lithium.ui.network.admin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;
import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.factory.XmlModelFactory;

@Configuration
@ConfigurationProperties(prefix = "wro4j", ignoreUnknownFields = false)
@Slf4j
public class Wro4jConfiguration {
	
	private String header = "Expires: Mon, 1 Jan 2018 00:00:00 GMT | Cache-Control: public, max-age=120";
	
	@Bean
	FilterRegistrationBean webResourceOptimizer(Environment env) throws Exception {
		FilterRegistrationBean fr = new FilterRegistrationBean();
		ConfigurableWroFilter filter = new ConfigurableWroFilter();
		ConfigurableWroManagerFactory factory = new ConfigurableWroManagerFactory() {
			
			@Override
			protected WroModelFactory newModelFactory() {
				return new XmlModelFactory() {
					@Override
					protected InputStream getModelResourceAsStream() throws IOException {
						return getClass().getResourceAsStream("/wro.xml");
					}
				};
			}
			
		};
		filter.setWroManagerFactory(factory);
		WroConfiguration c = new WroConfiguration();
		c.setIgnoreMissingResources(false);
		c.setDebug(false);
		c.setHeader(header);
		filter.setConfiguration(c);
		fr.setFilter(filter);
		fr.addUrlPatterns("/wro/*");
		return fr;
	}

}