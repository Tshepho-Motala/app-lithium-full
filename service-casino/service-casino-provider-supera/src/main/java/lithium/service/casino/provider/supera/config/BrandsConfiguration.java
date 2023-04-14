package lithium.service.casino.provider.supera.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="supera.endpoint")
public class BrandsConfiguration {
	
	/**
	 * Test to see if subobjects work
	 */
	private Map<Long, BrandsConfigurationBrand> brand = new HashMap<Long, BrandsConfigurationBrand>();
	
	public Map<Long, BrandsConfigurationBrand> getBrand() {
		return brand;
	}
	
}
