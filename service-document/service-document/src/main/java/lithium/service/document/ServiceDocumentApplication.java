package lithium.service.document;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import lithium.client.changelog.EnableChangeLogService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@LithiumService
@EnableLithiumServiceClients
@EnableMailStream
@EnableChangeLogService
@EnableCustomHttpErrorCodeExceptions
@EnableDomainClient
@EnableUserApiInternalClientService
@EnableLimitInternalSystemClient
@EnableLeaderCandidate
@EnableJpaAuditing
public class ServiceDocumentApplication extends LithiumServiceApplication {
	@Autowired
	private ServiceDocumentInit initStuff;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceDocumentApplication.class, args);
	}

	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		initStuff.init();
	}
}
