package lithium.service.event;

import lithium.leader.EnableLeaderCandidate;
import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.web.bind.annotation.RequestMapping;

import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;

@LithiumService
@EnableLithiumServiceClients
@EnableMailStream
@EnableLeaderCandidate
@Slf4j
public class ServiceEventApplication extends LithiumServiceApplication {
	
	@Autowired private LithiumServiceClientFactory services;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceEventApplication.class, args);
	}
	
	@Override
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		
		try {
			loadData();
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	@RequestMapping("/loaddata")
	public void loadData() throws Exception {

	}

}
