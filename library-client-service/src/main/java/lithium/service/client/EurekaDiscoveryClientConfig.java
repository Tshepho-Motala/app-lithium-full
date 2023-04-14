package lithium.service.client;

import com.netflix.discovery.EurekaClientConfig;
import lombok.EqualsAndHashCode;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaDiscoveryClientConfig {

  @Bean
  public EurekaClientConfig eurekaClientConfig(){
    return new FailsIfCantFetchRegistryAtInitEurekaClientConfigBean();
  }

  /**
   * The default behaviour that fails the app during start if can't fetch the full services registry from Eureka can be disabled by setting
   * eureka.client.shouldEnforceFetchRegistryAtInit=false
   * Pay attention that if the app continues to work without fetching the full registry at the startup it may produce UnknownHost exceptions later
   * trying to reach remote services as the default strategy for EurekaClient is to receive only registry deltas after
   * the full initial fetch of the registry. The last one can be disabled by setting eureka.client.disable-delta=true
   */
  @EqualsAndHashCode(callSuper = true)
  private static class FailsIfCantFetchRegistryAtInitEurekaClientConfigBean extends EurekaClientConfigBean{

    private boolean shouldEnforceFetchRegistryAtInit = true;

    @Override
    public boolean shouldEnforceFetchRegistryAtInit() {
      return this.shouldEnforceFetchRegistryAtInit;
    }

    public void setShouldEnforceFetchRegistryAtInit(boolean shouldEnforceRegistrationAtInit) {
      this.shouldEnforceFetchRegistryAtInit = shouldEnforceRegistrationAtInit;
    }
  }

}
