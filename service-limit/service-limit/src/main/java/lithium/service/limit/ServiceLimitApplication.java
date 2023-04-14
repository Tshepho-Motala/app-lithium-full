package lithium.service.limit;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.accounting.client.service.EnableAccountingClientService;
import lithium.service.accounting.client.transactiontyperegister.EnableTransactionTypeRegisterService;
import lithium.service.cashier.client.service.EnableCashierInternalClientService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.limit.client.stream.EnablePromotionRestrictionTriggerStream;
import lithium.service.limit.client.stream.EnableUserRestrictionTriggerStream;
import lithium.service.limit.services.PlayerBalanceLimitService;
import lithium.service.limit.services.RestrictionService;
import lithium.service.limit.services.SystemRestrictionService;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.service.sms.client.stream.EnableSMSStream;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableLithiumServiceClients
@EnableChangeLogService
@EnableDomainClient
@EnableJpaAuditing
@EnableMailStream
@EnableSMSStream
@EnableNotificationStream
@EnableLeaderCandidate
@EnableAccountingClientService
@EnableUserApiInternalClientService
@EnableCustomHttpErrorCodeExceptions
@EnableTransactionTypeRegisterService
@EnableCashierInternalClientService
@EnableUserRestrictionTriggerStream
@EnablePromotionRestrictionTriggerStream
@EnableScheduling
public class ServiceLimitApplication extends LithiumServiceApplication {
	@Autowired
	SystemRestrictionService systemRestrictionService;
	@Autowired
	PlayerBalanceLimitService playerBalanceLimitService;
	@Autowired
	RestrictionService restrictionService;


	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceLimitApplication.class, args);
	}

	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);

		systemRestrictionService.createSystemRestrictions();
		playerBalanceLimitService.startup();
	}
}
