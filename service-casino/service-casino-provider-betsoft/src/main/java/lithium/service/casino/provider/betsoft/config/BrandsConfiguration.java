package lithium.service.casino.provider.betsoft.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="betsoft.endpoint")
public class BrandsConfiguration {
	
	/**
	 * Test to see if subobjects work
	 */
	private Map<Long, BrandsConfigurationBrand> brand = new HashMap<Long, BrandsConfigurationBrand>();
	
	public Map<Long, BrandsConfigurationBrand> getBrand() {
		return brand;
	}
	
}
