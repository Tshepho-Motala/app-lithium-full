package lithium.service.access.provider.gamstop;

import java.util.ArrayList;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;
import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lombok.Getter;

@RestController
public class ServiceAccessProviderGamstopModuleInfo extends ModuleInfoAdapter {

	ServiceAccessProviderGamstopModuleInfo() {
		super();
		//Arraylist containing all the relevant properties for the provider
		ArrayList<ProviderConfigProperty> properties= new ArrayList<>();

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.PLATFORM_URL.getValue())
				.required(true)
				.tooltip("Platform url as provided on Gamstop site")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.API_KEY.getValue())
				.required(true)
				.tooltip("Api Key provided by Gamstop")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.BATCH_PLATFORM_URL.getValue())
				.required(true)
				.tooltip("Gamstop batch platform url")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.CONNECTION_REQUEST_TIMEOUT.getValue())
				.required(false)
				.tooltip("The timeout in milliseconds used when requesting a connection from the connection manager. A timeout value of zero is interpreted as an infinite timeout.")
				.dataType(Integer.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.CONNECT_TIMEOUT.getValue())
				.required(false)
				.tooltip("Determines the timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an infinite timeout.")
				.dataType(Integer.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.SOCKET_TIMEOUT.getValue())
				.required(false)
				.tooltip("Defines the socket timeout in milliseconds, which is the timeout for waiting for data  or, put differently, the maximum period of inactivity between two consecutive data packets. A timeout value of zero is interpreted as an infinite timeout.")
				.dataType(Integer.class)
				.version(1)
				.build());
		
		//Add the provider to moduleinfo
		addProvider(
			ProviderConfig.builder()
			.name(getModuleName())
			.type(ProviderType.ACCESS)
			.properties(properties)
			.build()
		);
	}
	
	public static enum ConfigProperties {
		PLATFORM_URL ("platformUrl"),
		API_KEY ("apiKey"),
		BATCH_PLATFORM_URL ("batchPlatformUrl"),
		CONNECTION_REQUEST_TIMEOUT("connectionRequestTimeout"),
		CONNECT_TIMEOUT("connectTimeout"),
		SOCKET_TIMEOUT("socketTimeout");

		@Getter
		private final String value;
		
		ConfigProperties(String valueParam) {
			value = valueParam;
		}
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/{providerUrl}/{domainName}/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/{providerUrl}/{domainName}/**").authenticated();
		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
	}
}