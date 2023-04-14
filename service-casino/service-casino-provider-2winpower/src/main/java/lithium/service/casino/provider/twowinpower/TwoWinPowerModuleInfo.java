package lithium.service.casino.provider.twowinpower;

import java.util.ArrayList;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.Getter;

@RestController
public class TwoWinPowerModuleInfo extends ModuleInfoAdapter {
	TwoWinPowerModuleInfo() {
		super();
		// Arraylist containing all the relevant properties for the provider
		ArrayList<ProviderConfigProperty> properties = new ArrayList<ProviderConfigProperty>();
		properties.add(ProviderConfigProperty.builder().name(ConfigProperties.BASE_URL.getValue()).required(true)
			.tooltip("Base URL used for service calls to nucleus").dataType(String.class).version(1).build());
		
		properties.add(ProviderConfigProperty.builder().name(ConfigProperties.MERCHANT_ID.getValue()).required(true)
			.tooltip("Bank id provided by nucleus for specific domain").dataType(String.class).version(1).build());
		
		properties.add(ProviderConfigProperty.builder().name(ConfigProperties.MERCHANT_KEY.getValue()).required(true)
			.tooltip("Bank id provided by nucleus for specific domain").dataType(String.class).version(1).build());
		
		properties.add(ProviderConfigProperty.builder().name(ConfigProperties.API_KEY.getValue()).required(true)
			.tooltip("API key provided to nucleus for endpoint URL access").dataType(String.class).version(1).build());
		
		properties.add(ProviderConfigProperty.builder().name(ConfigProperties.IMAGE_URL.getValue()).required(true)
			.tooltip("URL where the images for the nucleus games are located. Images need to conform to naming conventions").dataType(String.class).version(1).build());
		
		properties.add(ProviderConfigProperty.builder().name(ConfigProperties.CURRENCY.getValue()).required(true)
			.tooltip("The currency that this domain will be working with.").dataType(String.class).version(1).build());
		
		// Add the provider to moduleinfo
		addProvider(ProviderConfig.builder().name(getModuleName()).type(ProviderType.CASINO).properties(properties).build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		// TODO: Add api authentication in for provider security (perhaps use provider-name as well)
		http.authorizeRequests().antMatchers("/{providerUrl}/{apiKey}/{domainName}/**").access("@lithiumSecurity.authenticatedApi(#apiKey)");
		http.authorizeRequests().antMatchers("/games/**").access("@lithiumSecurity.authenticateSystem(authentication)");
	}
	
	public static enum ConfigProperties {
		BASE_URL("baseUrl"), MERCHANT_ID("merchantId"), MERCHANT_KEY("merchantKey"), API_KEY("apikey"), IMAGE_URL("imageUrl"), CURRENCY("currency");
		
		@Getter
		private final String value;
		
		ConfigProperties(String valueParam) {
			value = valueParam;
		}
	}
}
