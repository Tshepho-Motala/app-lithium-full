package lithium.service.casino.client;

import lithium.service.casino.client.objects.StartGameMock;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public abstract class Mockable {
	@Value("${spring.application.name}")
	protected String applicationName;

	@Autowired
	protected LithiumServiceClientFactory services;

	/**
	 * Return either the original url for game start or the mock url if the mock service is available and switched on
	 */
	public String mockGameStartIfParameterIsSet(final boolean mockActive, final String url, final String authToken, final String providerGameId, final String currency) {

		if (mockActive) {
			try {
				CasinoGenericMock genericMockClient = services.target(CasinoGenericMock.class, "service-casino-mock-all", true);
				String mockUrl = genericMockClient.startGame(StartGameMock.builder()
						.startGameUrl(url)
						.authToken(authToken)
						.gameProviderGuid(applicationName)
						.providerGameId(providerGameId)
						.currency(currency)
						.build());
				log.info("Original url: " + url);
				log.info("Mock URL for game start: " + mockUrl);
				return mockUrl;
			} catch (Exception e) {
				log.debug("Problem getting casino mock all service");
			}
		}

		return url;
	}
}
