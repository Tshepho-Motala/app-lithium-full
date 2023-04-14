package lithium.service.user.provider.google;

import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableOAuth2Sso
@EnableLithiumServiceClients
public class ServiceUserProviderGoogleApplication extends LithiumServiceApplication {
	@Autowired
	private OAuth2RestTemplate oAuth2RestTemplate;
	
	@Bean
	public CustomOAuth2RestTemplate customOAuth2RestTemplate() {
		CustomOAuth2RestTemplate restTemplate = new CustomOAuth2RestTemplate();
		restTemplate.customize(oAuth2RestTemplate);
		return restTemplate;
	}
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceUserProviderGoogleApplication.class, args);
	}
}
