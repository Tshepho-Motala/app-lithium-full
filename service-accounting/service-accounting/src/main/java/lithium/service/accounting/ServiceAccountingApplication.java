package lithium.service.accounting;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableDomainClient
@EnableLimitInternalSystemClient
@EnableUserApiInternalClientService
@EnableCustomHttpErrorCodeExceptions
public class ServiceAccountingApplication extends LithiumServiceApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceAccountingApplication.class, args);
	}
}
