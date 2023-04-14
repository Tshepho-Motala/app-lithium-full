package lithium.service.limit.client.stream;

import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AutoRestrictionTriggerStream {
	@Autowired private AutoRestrictionTriggerOutputQueue queue;

	public void trigger(AutoRestrictionTriggerData data) {
		try {
			queue.outputQueue().send(MessageBuilder.<AutoRestrictionTriggerData>withPayload(data).build());
		} catch(RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}

}