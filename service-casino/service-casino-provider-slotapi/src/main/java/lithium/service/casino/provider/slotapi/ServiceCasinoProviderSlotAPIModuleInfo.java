package lithium.service.casino.provider.slotapi;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.casino.provider.slotapi.config.ProviderConfigProperties;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

import java.util.ArrayList;

@RestController
public class ServiceCasinoProviderSlotAPIModuleInfo extends ModuleInfoAdapter {
	ServiceCasinoProviderSlotAPIModuleInfo() {
		super();
		//Arraylist containing all the relevant properties for the provider
		ArrayList<ProviderConfigProperty> properties= new ArrayList<ProviderConfigProperty>();

		properties.add(ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.HASH_PASSWORD.getValue())
				.required(true)
				.tooltip("Password used to calculate transaction integrity hash")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.BET_HISTORY_ROUND_DETAIL_URL.getValue())
				.required(true)
				.tooltip("The URL for the bet history round detail service")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.BET_HISTORY_ROUND_DETAIL_PROVIDER_ID.getValue())
				.required(true)
				.tooltip("The provider ID to be used with the bet history round detail service")
				.dataType(String.class)
				.version(1)
				.build());
		
		//Add the provider to moduleinfo
		addProvider(ProviderConfig.builder()
				.name(getModuleName())
				.type(ProviderType.CASINO)
				.properties(properties)
				.build());

		roles();
	}

	private void roles() {
		Category casinoCategory = Category.builder().name("Casino Search Operations").description("Operations related to searching of casino bets.").build();
		addRole(Role.builder().category(casinoCategory).name("Player Casino History").role("PLAYER_CASINO_HISTORY").description("Search Player Casino History").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests()
			.antMatchers("/openapi.yaml").permitAll()
			.antMatchers("/validatesession").authenticated()
			.antMatchers("/balance").authenticated()
			.antMatchers("/bet").authenticated()
			.antMatchers("/backoffice/history/**").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_CASINO_HISTORY')")
			.antMatchers("/betresult").permitAll() // SHA256 HMAC PSK Auth
			.antMatchers("/bonus/trigger").permitAll() // SHA256 HMAC PSK Auth + Basic Token
			.antMatchers("/bonus/{bonusType}/find/active").permitAll() // SHA256 HMAC PSK Auth + Basic Token
			.antMatchers("/frontend/**").authenticated()
			.antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		;
	}
}
