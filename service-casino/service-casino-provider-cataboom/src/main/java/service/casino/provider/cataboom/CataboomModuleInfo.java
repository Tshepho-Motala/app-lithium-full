package service.casino.provider.cataboom;

import java.util.ArrayList;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.Getter;

@RestController
public class CataboomModuleInfo extends ModuleInfoAdapter {
	CataboomModuleInfo() {
		super();
		// Arraylist containing all the relevant properties for the provider
		ArrayList<ProviderConfigProperty> properties = new ArrayList<ProviderConfigProperty>();

		properties.add(ProviderConfigProperty.builder().name(ConfigProperties.BASE_URL.getValue()).required(false)
				.tooltip("cataboom site").dataType(String.class).version(1).build());

		// Add the provider to moduleinfo
		addProvider(ProviderConfig.builder().name(getModuleName()).type(ProviderType.CASINO).properties(properties)
				.build());
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		
		//Real mappings
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/cataboomcampaigns/{domainName}/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/cataboomcampaigns/{domainName}/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/cataboom/prizefulfill/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/cataboom/prizefulfill/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/{providerUrl}/{domainName}/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/{providerUrl}/{domainName}/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/process/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/process/**").authenticated();

		//Bonus linking method
		http.authorizeRequests().antMatchers("/casino/externalBonusGame/**").access("@lithiumSecurity.authenticatedSystem(authentication)");;
		
		//mock mappings
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/mock/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/mock/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/getPrizeInfo/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/getPrizeInfo/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/dplay/{campaignid}/{uuid}/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/dplay/{campaignid}/{uuid}/**").permitAll();
		
	//	http.authorizeRequests().anyRequest().permitAll();

	}

	public static enum ConfigProperties {
		 MOCK_FLAG("mockFlag"),
		BASE_URL("baseurl");

		@Getter
		private final String value;

		ConfigProperties(String valueParam) {
			value = valueParam;
		}
	}
}
