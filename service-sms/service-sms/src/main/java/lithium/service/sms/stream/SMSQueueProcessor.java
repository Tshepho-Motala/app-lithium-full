package lithium.service.sms.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.sms.services.SMSService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(SMSQueueSink.class)
@Slf4j
public class SMSQueueProcessor {
	@Autowired SMSService smsService;
	
	@StreamListener(SMSQueueSink.INPUT) 
	void handle(lithium.service.sms.client.objects.SMSBasic smsBasic) throws Exception {
		log.info("Received sms from queue for processing for " + smsBasic.getUserGuid());
		log.debug("Received sms from queue for processing :: " + smsBasic);
		smsService.save(
			true,
			smsBasic.getDomainName(),
			smsBasic.getSmsTemplateName(),
			smsBasic.getSmsTemplateLang(),
			smsBasic.getTo(),
			smsBasic.getPriority(),
			smsBasic.getUserGuid(),
			smsBasic.resolvePlaceholders()
		);
	}
}
