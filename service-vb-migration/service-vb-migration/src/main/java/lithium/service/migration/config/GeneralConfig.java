package lithium.service.migration.config;

import com.google.gson.Gson;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.vb.migration.SystemHistoricRegistrationIngestionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
public class GeneralConfig {

  @Autowired
  LithiumServiceClientFactory services;

  @Bean
  public SystemHistoricRegistrationIngestionClient systemPlayerBasicClient() throws Exception {
    return services.target(SystemHistoricRegistrationIngestionClient.class);
  }

  @Bean
  public Gson gson(){
    return new Gson();
  }

  // Huh? @Faiz I removed this because it messes with lithium security.
//  @Bean
//  public TokenStore tokenStore() {
//    return new InMemoryTokenStore();
//  }

}
