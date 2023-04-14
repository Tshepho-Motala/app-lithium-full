package lithium.service.casino.provider.twowinpower;

import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import lithium.service.casino.provider.twowinpower.config.ConfigurationPropertiesTWP;
import lithium.service.casino.provider.twowinpower.service.GamesUpdateService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.games.stream.EnableGamesStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LithiumService
@EnableGamesStream
@EnableLithiumServiceClients
@EnableConfigurationProperties(ConfigurationPropertiesTWP.class)
public class ServiceCasinoProviderTwoWinPowerApplication extends LithiumServiceApplication {
	@Autowired
	private GamesUpdateService service;
	@Autowired
	private ConfigurationPropertiesTWP config;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoProviderTwoWinPowerApplication.class, args);
	}
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		if (config.loadGamesOnStartup()) {
			log.info("TWP Startup. Updating games now!");
			service.updateGames("default", config.getDownloadImages());
		} else {
			log.info("Games not updating from provider. (load-games-on-startup property set to false/missing)");
		}
	}
}
