package lithium.service.mail.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.mail.services.MailService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(SystemMailQueueSink.class)
@Slf4j
public class SystemMailQueueProcessor {
	@Autowired MailService mailService;
	
	@StreamListener(SystemMailQueueSink.INPUT) 
	void handle(lithium.service.mail.client.objects.SystemEmailData systemEmailData) throws Exception {
		log.debug("Received system mail from queue for processing :: " + systemEmailData.toString());
		log.info("Received system mail from queue for processing ::"+ systemEmailData.getUserGuid());
		mailService.saveSystemEmail(systemEmailData);
	}
}