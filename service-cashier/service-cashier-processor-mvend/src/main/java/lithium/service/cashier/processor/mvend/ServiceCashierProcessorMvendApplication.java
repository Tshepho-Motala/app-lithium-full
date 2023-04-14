package lithium.service.cashier.processor.mvend;

import lithium.service.accounting.client.service.EnableAccountingClientService;
import lithium.service.cashier.LithiumServiceProcessorApplication;
import lithium.service.cashier.client.service.EnableCashierInternalClientService;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.application.LithiumShutdownSpringApplication;

/** See LIVESCORE-227 for reference handling */

@LithiumService
@EnableCashierInternalClientService
@EnableUserApiInternalClientService
@EnableAccountingClientService
@EnableDomainClient
@EnableLimitInternalSystemClient
public class ServiceCashierProcessorMvendApplication extends LithiumServiceProcessorApplication {
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierProcessorMvendApplication.class, args);
	}
}
