package lithium.service.changelog;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.changelog.config.ChangeLogConfigurationProperties;
import lithium.service.changelog.services.CategoryService;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.util.EnableLocaleContextProcessor;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

@LithiumService
@EnableConfigurationProperties(ChangeLogConfigurationProperties.class)
@EnableLeaderCandidate
@EnableLocaleContextProcessor
@EnableDomainClient
public class ServiceChangelogApplication extends LithiumServiceApplication {

	@Autowired CategoryService categoryService;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceChangelogApplication.class, args);
	}

	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		categoryService.setupFromEnums();
	}
}
