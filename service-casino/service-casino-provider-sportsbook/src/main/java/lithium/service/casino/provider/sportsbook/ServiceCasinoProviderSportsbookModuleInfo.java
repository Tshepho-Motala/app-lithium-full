package lithium.service.casino.provider.sportsbook;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigProperties;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

import java.util.ArrayList;

@RestController
public class ServiceCasinoProviderSportsbookModuleInfo extends ModuleInfoAdapter {
	ServiceCasinoProviderSportsbookModuleInfo() {
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
				.name(ProviderConfigProperties.EXTERNAL_TRANSACTION_INFO_URL.getValue())
				.required(true)
				.tooltip("The {url}/betinfo endpoint url exposed by the sportsbook provider, not including /betinfo")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.PLAYER_OFFSET.getValue())
				.required(false)
				.tooltip("Environment specific offset number to be added to the player id ex 10000")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.BETSEARCH_URL.getValue())
				.required(false)
				.tooltip("The endpoint url exposed by the sportsbook provider for bet searching")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.BETSEARCH_KEY.getValue())
				.required(false)
				.tooltip("The pre-shared key provided by the sportsbook provider for bet searching")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.BETSEARCH_BRAND.getValue())
				.required(true)
				.tooltip("The brand provided by the sportsbook provider for bet searching")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.SPORTS_FREE_BETS_URL.getValue())
				.required(false)
				.tooltip("The url endpoint exposed by the sportsbook provider for freebets")
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.BONUS_RESTRICTION_URL.getValue())
				.required(false)
				.tooltip("The url endpoint exposed for restricting a player bonus")
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.BONUS_RESTRICTION_KEY.getValue())
				.required(false)
				.tooltip("The pre-shared key for generating the bonus restriction endpoint hash")
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
		Category betSearchCategory = Category.builder().name("Bet Search Operations").description("Operations related to searching of sportsbook bets.").build();
		addRole(Role.builder().category(betSearchCategory).name("Sports Bet History").role("ROLE_SPORTS_BET_HISTORY").description("Search Sports Bet History").build());
		addRole(Role.builder().category(betSearchCategory).name("Player Sports Bet History").role("ROLE_PLAYER_SPORTS_BET_HISTORY").description("Search Player Sports Bet History").build());
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests()
			.antMatchers("/openapi.yaml").permitAll()
			.antMatchers("/bet/reserve").permitAll() // Protected via sha256 pre-shared key
			.antMatchers("/bet/cancelreserve").permitAll() // Protected via sha256 pre-shared key
			.antMatchers("/bet/debitreserve").permitAll() // Protected via sha256 pre-shared key
			.antMatchers("/bet/commitreserve").permitAll() // Protected via sha256 pre-shared key
			.antMatchers("/settle/debit").permitAll() // Protected via sha256 pre-shared key
			.antMatchers("/settle/credit").permitAll() // Protected via sha256 pre-shared key
			.antMatchers("/settle").permitAll() // Protected via sha256 pre-shared key
			.antMatchers("/validatetoken").authenticated()
			.antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");


		http.authorizeRequests().antMatchers("/backoffice/bet/{domainName}/search/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ROLE_SPORTS_BET_HISTORY', 'ROLE_PLAYER_SPORTS_BET_HISTORY')");
		http.authorizeRequests().antMatchers("/backoffice/bet/{domainName}/freebets/history").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ROLE_SPORTS_BET_HISTORY', 'ROLE_PLAYER_SPORTS_BET_HISTORY')");
	}
}
