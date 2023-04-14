package lithium.service.cdn.provider.google;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

/**
 *
 */
@LithiumService
@EnableChangeLogService
@EnableDomainClient
@EnableLithiumServiceClients
@EnableUserApiInternalClientService
@EnableProviderClient
public class ServiceCdnProviderGoogleApplication extends LithiumServiceApplication {

  /**
   * @param args
   */
  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServiceCdnProviderGoogleApplication.class, args);
  }
}
