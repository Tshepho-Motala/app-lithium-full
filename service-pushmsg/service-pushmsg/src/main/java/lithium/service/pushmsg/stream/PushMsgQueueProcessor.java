package lithium.service.pushmsg.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.pushmsg.client.objects.PushMsgBasic;
import lithium.service.pushmsg.services.PushMsgService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(PushMsgQueueSink.class)
@Slf4j
public class PushMsgQueueProcessor {
	@Autowired PushMsgService pushMsgService;
	
	@StreamListener(PushMsgQueueSink.INPUT) 
	void handle(PushMsgBasic pushMsg) throws Exception {
		log.info("Received pushmsg from queue for processing :: " + pushMsg);
		pushMsgService.push(pushMsg);
	}
}