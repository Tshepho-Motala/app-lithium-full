package lithium.service.limit.stream;

import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.services.AutoRestrictionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding(AutoRestrictionTriggerQueueSink.class)
public class AutoRestrictionTriggerQueueProcessor {
	@Autowired private AutoRestrictionService service;

	@StreamListener(AutoRestrictionTriggerQueueSink.INPUT)
	public void trigger(AutoRestrictionTriggerData data) {
		log.debug("Received an auto-restriction trigger from the queue for processing: " + data);
		
		service.processAutoRestrictionRulesets(data.getUserGuid());
	}
}
