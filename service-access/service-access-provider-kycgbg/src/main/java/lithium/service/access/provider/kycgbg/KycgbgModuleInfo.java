package lithium.service.access.provider.kycgbg;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
public class KycgbgModuleInfo extends ModuleInfoAdapter {

	KycgbgModuleInfo() {
		super();
		//Arraylist containing all the relevant properties for the provider
		ArrayList<ProviderConfigProperty> properties= new ArrayList<ProviderConfigProperty>();
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.PROFILE_ID.getValue())
				.required(true)
				.tooltip("Profile ID")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.PEP_SANCTIONS_ID.getValue())
				.required(false)
				.tooltip("An additional profile that is used in case of PEP & Sanctions. If the function is not planned to be used, leave the field empty")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.USERNAME.getValue())
				.required(true)
				.tooltip("Account Username (Usually in form of email address)")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.PASSWORD.getValue())
				.required(true)
				.tooltip("Account Password")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.BASE_URL.getValue())
				.required(true)
				.tooltip("wsdl url as provided on id3Global site (Please do not insert ?wsdl at the end of the url")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(
			ProviderConfigProperty.builder()
			.name(ConfigProperties.TIMEOUT_READ.getValue())
			.required(false)
			.tooltip("Set the socket read timeout for the underlying HttpClient. A value of 0 means never timeout.")
			.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
			.name(ConfigProperties.TIMEOUT_CONNECTION.getValue())
			.required(false)
			.tooltip("Sets the timeout until a connection is established. A value of 0 means never timeout.")
			.build()
		);
		
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
		PROFILE_ID("Profile ID"),
		PEP_SANCTIONS_ID("pepSancID"),
		USERNAME ("username"),
		PASSWORD ("password"),
		BASE_URL ("baseUrl"),
		TIMEOUT_READ ("readTimeout"),
		TIMEOUT_CONNECTION("connectionTimeout");
		
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