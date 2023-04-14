package lithium.service.access.provider.iovation;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.access.provider.iovation.config.Config;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;

@Component
public class ServiceAccessProviderIovationModuleInfo extends ModuleInfoAdapter {
	public ServiceAccessProviderIovationModuleInfo() {
		super();
		addProvider(ProviderConfig.builder()
			.name(getModuleName())
			.type(ProviderType.ACCESS)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.BASE_URL.property())
				.tooltip("Base URL for the iovation endpoints.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.SUBSCRIBER_ID.property())
				.tooltip("Iovation subscriber id.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.SUBSCRIBER_ACCOUNT.property())
				.tooltip("Iovation subscriber account.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.SUBSCRIBER_PASSCODE.property())
				.tooltip("Iovation subscriber passcode.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.CONNECTION_REQUEST_TIMEOUT.property())
				.required(false)
				.tooltip("The timeout in milliseconds used when requesting a connection from the connection manager. A timeout value of zero is interpreted as an infinite timeout.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.CONNECT_TIMEOUT.property())
				.required(false)
				.tooltip("Determines the timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an infinite timeout.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.SOCKET_TIMEOUT.property())
				.required(false)
				.tooltip("Defines the socket timeout ({@code SO_TIMEOUT}) in milliseconds, which is the timeout for waiting for data  or, put differently, a maximum period inactivity between two consecutive data packets). A timeout value of zero is interpreted as an infinite timeout.")
				.build()
			)
			.build()
		);
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests().antMatchers("/{domainName}/iovation/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/*").permitAll();
		http.authorizeRequests().antMatchers("/fraud/**").permitAll();
	}
}
