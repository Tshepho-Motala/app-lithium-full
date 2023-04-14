package lithium.service.casino.provider.nucleus.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="nucleus.endpoint")
public class BrandsConfiguration {
	
	/**
	 * Test to see if subobjects work
	 */
	private Map<Long, BrandsConfigurationBrand> brand = new HashMap<Long, BrandsConfigurationBrand>();
	
	public Map<Long, BrandsConfigurationBrand> getBrand() {
		return brand;
	}
	
}
