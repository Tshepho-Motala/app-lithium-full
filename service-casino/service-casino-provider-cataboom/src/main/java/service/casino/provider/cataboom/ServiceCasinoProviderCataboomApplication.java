package service.casino.provider.cataboom;

import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.client.stream.EnableTriggerBonusStream;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import service.casino.provider.cataboom.Init;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLithiumServiceClients
@EnableTriggerBonusStream
@EnableDomainClient
public class ServiceCasinoProviderCataboomApplication extends LithiumServiceApplication{
	@Autowired
	private Init initStuff;
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoProviderCataboomApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		if (isLoadTestData()) {
			initStuff.init();
		}
	}
	}
