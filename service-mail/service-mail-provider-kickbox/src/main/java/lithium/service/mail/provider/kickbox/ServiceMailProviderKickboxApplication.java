package lithium.service.mail.provider.kickbox;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.rest.EnableRestTemplate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.mail.LithiumServiceProviderApplication;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 *
 */
@LithiumService
@EnableFeignClients
@EnableLithiumServiceClients
@EnableRestTemplate
@EnableCustomHttpErrorCodeExceptions
public class ServiceMailProviderKickboxApplication extends LithiumServiceProviderApplication {

  /**
   * @param args
   */
  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServiceMailProviderKickboxApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate(@Qualifier("lithium.rest") RestTemplateBuilder builder) {
    return builder.build();
  }
}

