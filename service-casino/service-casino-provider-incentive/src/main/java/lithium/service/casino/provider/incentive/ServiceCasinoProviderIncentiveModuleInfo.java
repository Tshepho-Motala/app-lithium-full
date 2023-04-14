package lithium.service.casino.provider.incentive;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.casino.provider.incentive.config.ProviderConfigProperties;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ServiceCasinoProviderIncentiveModuleInfo extends ModuleInfoAdapter {
	ServiceCasinoProviderIncentiveModuleInfo() {
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
		
		//Add the provider to moduleinfo
		addProvider(ProviderConfig.builder()
				.name(getModuleName())
				.type(ProviderType.CASINO)
				.properties(properties)
				.build());

		roles();
	}

	private void roles() {
		Category betsCategory = Category.builder().name("Bet Operations").description("Operations related to incentive game bets.").build();
		addRole(Role.builder().category(betsCategory).name("Incentive Game Bets View").role("INCENTIVEGAMES_BETS_VIEW").description("View Incentive Game Bets").build());
		addRole(Role.builder().category(betsCategory).name("Player Incentive Games View").role("PLAYER_INCENTIVE_GAME_VIEW").description("View incentive games for a player.").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests()
			.antMatchers("/openapi.yaml").permitAll()
			.antMatchers("/validatesession").authenticated()
			.antMatchers("/placement").authenticated()
			.antMatchers("/settlement").permitAll()
			.antMatchers("/pickany/settlement").permitAll()
			.antMatchers("/pickany/entry").authenticated()

			.antMatchers("/admin/bets/table").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_INCENTIVE_GAME_VIEW')")
			.antMatchers("/admin/bets/resultcodes").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_INCENTIVE_GAME_VIEW')")
			.antMatchers("/admin/bets/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'INCENTIVEGAMES_BETS_VIEW')")

			.antMatchers("/frontend/**").authenticated()
			.antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");

		;
	}
}
