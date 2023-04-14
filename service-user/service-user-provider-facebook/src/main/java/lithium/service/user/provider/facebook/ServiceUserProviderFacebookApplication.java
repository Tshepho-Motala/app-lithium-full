package lithium.service.user.provider.facebook;

import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.stream.EnableProvidersStream;
import lithium.service.user.provider.facebook.config.FacebookConfigurationProperties;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LithiumService
@EnableOAuth2Sso
@EnableProvidersStream
@EnableLithiumServiceClients
//@ComponentScan("lithium.service")
@EnableConfigurationProperties(FacebookConfigurationProperties.class)
public class ServiceUserProviderFacebookApplication extends LithiumServiceApplication {
//	@Autowired
//	private ProvidersStream providersStream;
	
	@EventListener
	public void startup(ApplicationStartedEvent event) throws Exception {
		log.trace("startup");
		super.startup(event);
//		try {
//			Provider user = Provider.builder()
//			.name("user-provider-facebook")
//			.enabled(false)
//			.url("service-user-provider-facebook")
//			.domain(Domain.builder().name("default").build())
//			.providerType(ProviderType.builder().name(ProviderType.PROVIDER_TYPE_USER).build())
//			.build();
//			log.info("Registering : "+user);
//			providersStream.registerProvider(user);
//			Provider auth = Provider.builder()
//			.name("auth-provider-facebook")
//			.enabled(false)
//			.url("service-user-provider-facebook")
//			.domain(Domain.builder().name("default").build())
//			.providerType(ProviderType.builder().name(ProviderType.PROVIDER_TYPE_AUTH).build())
//			.build();
//			log.info("Registering : "+auth);
//			providersStream.registerProvider(auth);
//		} catch (Exception e) {
//			log.error("Error during startup.", e);
//		}
	}
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceUserProviderFacebookApplication.class, args);
	}
}
