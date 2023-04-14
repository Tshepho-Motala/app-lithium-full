package lithium.service.accountinghistory;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.casino.EnableCasinoClient;
import lithium.service.casino.EnableSystemBonusClient;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.reward.client.EnableQueryRewardClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableCasinoClient
@EnableDomainClient
@EnableQueryRewardClient
@EnableSystemBonusClient
@EnableLimitInternalSystemClient
@EnableUserApiInternalClientService
@EnableCustomHttpErrorCodeExceptions
public class ServiceAccountingHistoryApplication extends LithiumServiceApplication {

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServiceAccountingHistoryApplication.class, args);
  }
}
