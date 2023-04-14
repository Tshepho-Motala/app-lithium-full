package lithium.service.affiliate;

import lithium.leader.EnableLeaderCandidate;
import lithium.service.affiliate.services.TestDataService;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;

@LithiumService
@EnableLeaderCandidate
@EnableScheduling
@EnableMailStream
@EnableDomainClient
public class ServiceAffiliatePrvIncomeAccessApplication extends LithiumServiceApplication {
	
	@Autowired
	TestDataService testDataService;

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceAffiliatePrvIncomeAccessApplication.class, args);
	}
	
	@Override
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);

		if (isLoadTestData()) {
			testDataService.load();
		}
	}
}
