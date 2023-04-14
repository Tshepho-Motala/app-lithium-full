package lithium.service.casino.provider.supera;

import java.util.ArrayList;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.Getter;
import lithium.service.client.provider.ProviderConfig.ProviderType;

@RestController
public class SuperaModuleInfo extends ModuleInfoAdapter {
	SuperaModuleInfo() {
		super();
		//Arraylist containing all the relevant properties for the provider
		ArrayList<ProviderConfigProperty> properties= new ArrayList<ProviderConfigProperty>();
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.BASE_URL.getValue())
				.required(true)
				.tooltip("Base URL used for service calls to supera")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.SALT_KEY.getValue())
				.required(true)
				.tooltip("Key used to calculate transaction integrity hash (shared with supera)")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.API_USER.getValue())
				.required(true)
				.tooltip("Api username provided by supera for specific domain")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.API_PASSWORD.getValue())
				.required(true)
				.tooltip("Api password provided by supera for specific domain")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.API_KEY.getValue())
				.required(true)
				.tooltip("API key provided to supera for endpoint URL access")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.IMAGE_URL.getValue())
				.required(true)
				.tooltip("URL where the images for the supera games are located. Images need to conform to naming conventions")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.GAME_LIST_URL.getValue())
				.required(true)
				.tooltip("URL where the gamelist for the supera games are located. Not provided by supera.")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.CURRENCY.getValue())
				.required(true)
				.tooltip("The currency that this domain will be working with.")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.MOCK_FLAG.getValue())
				.required(false)
				.tooltip("Type 'true' for mock, blank or 'false' for no mock")
				.dataType(String.class)
				.version(1)
				.build());
		
		//Add the provider to moduleinfo
		addProvider(ProviderConfig.builder()
				.name(getModuleName())
				.type(ProviderType.CASINO)
				.properties(properties)
				.build());
		
		
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		//TODO: Add api authentication in for provider security (perhaps use provider-name as well)
		http.authorizeRequests().antMatchers("/{providerUrl}/{apiKey}/{domainName}/**").access("@lithiumSecurity.authenticatedApi(#apiKey)");
		http.authorizeRequests().antMatchers("/games/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/casino/mock/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
	}
	
	public static enum ConfigProperties {
		BASE_URL ("baseUrl"),
		SALT_KEY ("saltKey"),
		API_KEY ("apikey"),
		IMAGE_URL ("imageUrl"),
		GAME_LIST_URL ("gameListUrl"),
		CURRENCY ("currency"),
		API_PASSWORD("apiPassword"),
		API_USER ("apiUser"),
		MOCK_FLAG ("mockFlag");
		
		@Getter
		private final String value;
		
		ConfigProperties(String valueParam) {
			value = valueParam;
		}
	}
}
