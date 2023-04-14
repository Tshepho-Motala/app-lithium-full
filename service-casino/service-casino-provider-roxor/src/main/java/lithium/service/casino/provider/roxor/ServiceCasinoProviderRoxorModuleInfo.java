package lithium.service.casino.provider.roxor;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.casino.provider.roxor.config.ProviderConfigProperties;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ServiceCasinoProviderRoxorModuleInfo extends ModuleInfoAdapter {
	public ServiceCasinoProviderRoxorModuleInfo() {
		super();
		//Arraylist containing all the relevant properties for the provider
		List<ProviderConfigProperty> properties = new ArrayList<>();

		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.LAUNCH_URL.getValue())
				.tooltip("Gamesys games can be loaded into an appropriate container used by the third-party platforms via a game launch URL.")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.SOUND.getValue())
				.required(false)
				.tooltip("When the game is launched it will set the sound appropriately based on this value.")
				.dataType(Boolean.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.HOME_BUTTON.getValue())
				.required(false)
				.tooltip("Tells the game to display a home button allowing the player to return to the operators home page.")
				.dataType(Boolean.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.HOME_POS.getValue())
				.required(false)
				.tooltip("On which side of the screen to align the home/profile button. Optional. Defaults to left.")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.BALANCE_POS.getValue())
				.required(false)
				.tooltip("On which side of the screen to align the balance display. Optional. Defaults to left.")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.CHAT_HOST.getValue())
				.required(false)
				.tooltip("An optional URL-encoded URL that is used to load the chat client. If this parameter is missing and chat is enabled, then chat will not be loaded..")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.CHAT_CONTEXT.getValue())
				.required(false)
				.tooltip("An optional context that is passed to the chat client.")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.SESSION_REMINDER_INTERVAL.getValue())
				.required(false)
				.tooltip("Interval of Session Reminder Alert in seconds. If this is not specified, an alert will not appear..")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.SESSION_ELAPSED.getValue())
				.required(false)
				.tooltip("Current session time in seconds. This will be used by the wrapper to calculate when the next Session Reminder Alert will be shown. If this is not specified the wrapper will assume 0.")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.HOME_PAGE_URL.getValue())
				.required(false)
				.tooltip("An optional URL-encoded URL that the wrapper will redirect to if player presses Home button. If this is not specified, the wrapper will send a ShowHome console event instead.")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.DEPOSIT_PAGE_URL.getValue())
				.required(false)
				.tooltip("An optional URL-encoded URL that the wrapper will redirect to if player presses Deposit button on the \"Not enough funds\" alert. If this is not specified, the wrapper will send a ShowDeposit console event instead.")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.LOBBY_PAGE_URL.getValue())
				.required(false)
				.tooltip("An optional URL-encoded URL that the wrapper will redirect to if player presses \"Lobby\" button on the \"Not logged in\" alert. If this is not specified, the wrapper will send a ShowHome console event instead.")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.TRANSACTION_URL.getValue())
				.required(false)
				.tooltip("An optional URL-encoded URL that the wrapper will redirect to if player presses \"Transaction History\" button. If this is not specified, the wrapper will send a ShowTransactionHistory console event instead.")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.LOGOUT_URL.getValue())
				.required(false)
				.tooltip("An optional URL-encoded URL that the wrapper will redirect to if " +
					"player presses \"Logout\" button. If this is not specified, the wrapper will " +
					"send a ShowLogout console event instead.")
				.dataType(String.class)
				.build()
		);
		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.LOGIN_PAGE_URL.getValue())
				.required(false)
				.tooltip("An optional URL-encoded URL that the wrapper will redirect to if " +
					"player presses \"Login\" button on the \"Not logged in\" alert. If this is not " +
					"specified, the wrapper will send a ShowLogin console event instead.")
				.dataType(String.class)
				.build()
		);

		properties.add(
			ProviderConfigProperty.builder()
				.name(ProviderConfigProperties.GAME_API_URL.getValue())
				.required(false)
				.tooltip("The Gamesys Game API defines a JSON-based RPC interface that allows Gamesys games to interact with " +
					"third-party wallets and platforms. The API includes endpoints to record gameplay and wallet " +
					"operations.")
				.dataType(String.class)
				.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.WEBSITE.getValue())
						.required(false)
						.tooltip("Website of a player’s rewards e.g. website=jackpotjoy")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.IP_WHITE_LIST.getValue())
						.required(false)
						.tooltip("Comma delimited list of IP addresses allowed to invoke provider services")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.GAME_LIST_URL.getValue())
						.required(false)
						.tooltip("URL where the manually built roxor game list is located. Game list needs to conform to structural conventions")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.REWARDS_URL.getValue())
						.required(false)
						.tooltip("Roxor RGP-Rewards System Host URL.")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.REWARDS_DEFAULT_DURATION_IN_HOURS.getValue())
						.required(false)
						.tooltip("Roxor Default duration for rewards in hours e.g. 30 days x 24 hours = 720.")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.PROGRESSIVE_URL.getValue())
						.required(false)
						.tooltip("Roxor RGP-Progressive System Host URL.")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.ADD_CLOCK.getValue())
						.required(false)
						.tooltip("Whether we should add a clock to the URL sent to Roxor.")
						.dataType(Boolean.class)
						.build()
		);

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

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.BET_ROUND_REPLAY_URL.getValue())
						.required(false)
						.tooltip("The URL for the bet round replay service.")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.USE_PLAYER_API_TOKEN.getValue())
						.required(false)
						.tooltip("Whether we should use the API Token as the PlayerID to Roxor.")
						.dataType(Boolean.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.USE_PLAYER_ID_FROM_GUID.getValue())
						.required(false)
						.tooltip("Whether we should extract the PlayerID to from GUID for using it in rewards requests to Roxor.")
						.dataType(Boolean.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.FREE_GAMES_URL.getValue())
						.required(false)
						.tooltip("Roxor Games Availability Host URL.")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.EVOLUTION_DIRECT_GAME_LAUNCH_API_URL.getValue())
						.required(false)
						.tooltip("Evolution direct game launch api URL.")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.EVOLUTION_DIRECT_GAME_LAUNCH_API_CASINO_ID.getValue())
						.required(false)
						.tooltip("Evolution direct game launch api Casino ID.")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.EVOLUTION_DIRECT_GAME_LAUNCH_API_USERNAME.getValue())
						.required(false)
						.tooltip("Evolution direct game launch api Username.")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.EVOLUTION_DIRECT_GAME_LAUNCH_API_PASSWORD.getValue())
						.required(false)
						.tooltip("Evolution direct game launch api Password.")
						.dataType(String.class)
						.build()
		);

		properties.add(
				ProviderConfigProperty.builder()
						.name(ProviderConfigProperties.MICRO_GAMING_PROGRESSIVE_JACKPOT_FEED_URL.getValue())
						.required(false)
						.tooltip("Micro Gaming Progressive Jackpot Feed Url.")
						.dataType(String.class)
						.build()
		);

		ProviderConfig providerConfig = ProviderConfig.builder()
			.name(getModuleName())
			.type(ProviderConfig.ProviderType.CASINO)
			.properties(properties)
			.build();
		//Add the provider to moduleinfo
		addProvider(providerConfig);

		roles();
	}

	private void roles() {
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests()
			.antMatchers("/").permitAll()
			.antMatchers("/rgp/**").permitAll()
			.antMatchers("/games/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
			.antMatchers("/casino/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
			.antMatchers("/test/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
			.antMatchers(HttpMethod.GET, "/frontend/**").authenticated()
			.antMatchers("/system/jackpot-feed/progressive/{domainName}/{gameSupplier}/get").access("@lithiumSecurity.authenticatedSystem(authentication)")
		;
	}
}
