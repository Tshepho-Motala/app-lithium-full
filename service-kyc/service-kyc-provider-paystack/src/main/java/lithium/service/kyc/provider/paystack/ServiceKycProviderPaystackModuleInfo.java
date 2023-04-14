package lithium.service.kyc.provider.paystack;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.kyc.provider.config.KycTypeProviderProperty;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;

@RestController
public class ServiceKycProviderPaystackModuleInfo extends ModuleInfoAdapter {

	ServiceKycProviderPaystackModuleInfo() {
		super();
		//Arraylist containing all the relevant properties for the provider
		ArrayList<ProviderConfigProperty> properties= new ArrayList<>();

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.PLATFORM_URL.getValue())
				.required(true)
				.tooltip("Platform url as provided by paystack")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.API_KEY.getValue())
				.required(true)
				.tooltip("Secret Api Key provided by Paystack")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.BVN_LENGTH.getValue())
				.required(true)
				.tooltip("Minimum Bvn Length")
				.dataType(Long.class)
				.version(1)
				.build());

		properties.add(KycTypeProviderProperty.BVN);

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.CONNECTION_REQUEST_TIMEOUT.getValue())
				.required(false)
				.dataType(Integer.class)
				.tooltip("The timeout in milliseconds used when requesting a connection from the connection manager. A timeout value of zero is interpreted as an infinite timeout.")
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.CONNECT_TIMEOUT.getValue())
				.required(false)
				.dataType(Integer.class)
				.tooltip("Determines the timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an infinite timeout.")
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.SOCKET_TIMEOUT.getValue())
				.required(false)
				.dataType(Integer.class)
				.tooltip("Defines the socket timeout in milliseconds, which is the timeout for waiting for data  or, put differently, the maximum period of inactivity between two consecutive data packets. A timeout value of zero is interpreted as an infinite timeout.")
				.version(1)
				.build());

		//Add the provider to moduleinfo
		addProvider(
			ProviderConfig.builder()
			.name(getModuleName())
			.type(ProviderType.KYC)
			.properties(properties)
			.build()
		);
	}
	
	public static enum ConfigProperties {
		PLATFORM_URL ("platformUrl"),
		API_KEY ("apiKey"),
		BVN_LENGTH ("bvnLength"),
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
		http.authorizeRequests().antMatchers("/frontend/**").authenticated();
		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/backoffice/kyc/verify-bvn").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PAYSTACK_BVN_MANUAL_VERIFY')");
	}
}
