package lithium.service.cashier.provider.mercadonet;

import java.util.ArrayList;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.Getter;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@RestController
public class MercadonetModuleInfo extends ModuleInfoAdapter {
	MercadonetModuleInfo() {
		super();
		//Arraylist containing all the relevant properties for the provider
		ArrayList<ProviderConfigProperty> properties= new ArrayList<ProviderConfigProperty>();
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.BASE_URL.getValue())
				.required(true)
				.tooltip("Base URL used for service calls to mercadonet")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.CURRENCY.getValue())
				.required(true)
				.tooltip("Currency that is used by this cashier instance")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.API_KEY.getValue())
				.required(true)
				.tooltip("API key provided to betsoft for endpoint URL access")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.INSTANCE_ID.getValue())
				.required(true)
				.tooltip("The instance id provided by mercadonet for this domain")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.SKIN_ID.getValue())
				.required(true)
				.tooltip("The skin id provided by mercadonet for this domain")
				.dataType(String.class)
				.version(1)
				.build());
		
		//Add the provider to moduleinfo
		addProvider(ProviderConfig.builder()
				.name(getModuleName())
				.type(ProviderType.CASHIER)
				.properties(properties)
				.build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		//TODO: Add api authentication in for provider security
		http.authorizeRequests().antMatchers("/{providerUrl}/{apiKey}/{domainName}").access("@lithiumSecurity.authenticatedApi(#apiKey)");
		http.authorizeRequests().antMatchers("/cashier/startCashier").access("@lithiumSecurity.authenticatedSystem(authentication)");
	}
	
	public static enum ConfigProperties {
		BASE_URL ("baseUrl"),
		CURRENCY ("currency"),
		INSTANCE_ID ("instanceId"),
		API_KEY ("apikey"),
		SKIN_ID("skinId");
		
		@Getter
		private final String value;
		
		ConfigProperties(String valueParam) {
			value = valueParam;
		}
	}
}
